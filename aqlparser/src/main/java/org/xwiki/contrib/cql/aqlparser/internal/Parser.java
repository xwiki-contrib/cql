/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.cql.aqlparser.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xwiki.contrib.cql.aqlparser.AQLOperator;
import org.xwiki.contrib.cql.aqlparser.AQLParserState;
import org.xwiki.contrib.cql.aqlparser.ast.AQLAtomicClause;
import org.xwiki.contrib.cql.aqlparser.ast.AQLAtomicClauseOperator;
import org.xwiki.contrib.cql.aqlparser.ast.AQLBooleanLiteral;
import org.xwiki.contrib.cql.aqlparser.ast.AQLClauseOperator;
import org.xwiki.contrib.cql.aqlparser.ast.AQLClauseWithNextOperator;
import org.xwiki.contrib.cql.aqlparser.ast.AQLClausesWithNextOperator;
import org.xwiki.contrib.cql.aqlparser.ast.AQLDateLiteral;
import org.xwiki.contrib.cql.aqlparser.ast.AQLNumberLiteral;
import org.xwiki.contrib.cql.aqlparser.ast.AQLOrderByClause;
import org.xwiki.contrib.cql.aqlparser.ast.AQLStatement;
import org.xwiki.contrib.cql.aqlparser.ast.AbstractAQLAtomicValue;
import org.xwiki.contrib.cql.aqlparser.exceptions.ParserException;
import org.xwiki.contrib.cql.aqlparser.ast.AbstractAQLClause;
import org.xwiki.contrib.cql.aqlparser.ast.AbstractAQLRightHandValue;
import org.xwiki.contrib.cql.aqlparser.ast.AQLFunctionCall;
import org.xwiki.contrib.cql.aqlparser.ast.AQLInExpression;
import org.xwiki.contrib.cql.aqlparser.ast.AQLStringLiteral;

/**
 * The class that does the actual parsing.
 * @version $Id$
 * @since 0.0.1
 */
public class Parser
{
    private static final String AND = "and";

    private static final String OR = "or";

    private static final String NOT = "not";

    private static final String IN = "in";

    private static final String TRUE = "true";

    private static final String FALSE = "false";

    private static final Map<String, AQLOperator> ATOM_OPS_MAP = new LinkedHashMap<>();
    static {
        ATOM_OPS_MAP.put("=", AQLOperator.EQ);
        ATOM_OPS_MAP.put("!=", AQLOperator.NEQ);
        ATOM_OPS_MAP.put(">=", AQLOperator.GTE);
        ATOM_OPS_MAP.put("<=", AQLOperator.LTE);
        ATOM_OPS_MAP.put(">", AQLOperator.GT);
        ATOM_OPS_MAP.put("<", AQLOperator.LT);
        ATOM_OPS_MAP.put("~", AQLOperator.CONTAINS);
        ATOM_OPS_MAP.put("!~", AQLOperator.DOES_NOT_CONTAIN);
    }

    private static final String[] ATOM_OPS = ATOM_OPS_MAP.keySet().toArray(new String[0]);

    private final AQLReader reader;

    /**
     * @param aqlStatement the CQL statement to parse.
     */
    public Parser(String aqlStatement) throws ParserException
    {
        if (aqlStatement.isEmpty()) {
            throw new ParserException("The CQL expression is empty", new AQLParserState(0, 0, 0));
        }
        this.reader = new AQLReader(aqlStatement);
    }

    /**
     * Parse the CQL statement used to initialize the class. Can only be called once.
     * @return the AST corresponding to the aqlStatement.
     */
    public AQLStatement parse() throws ParserException, IOException
    {
        AQLParserState state = reader.getState();
        List<AQLClauseWithNextOperator> clausesWithNextOperator = parseClausesWithNextOperator();
        List<AQLOrderByClause> orderByClauses = maybeParseOrderByClauses();
        AQLStatement expression = new AQLStatement(state, clausesWithNextOperator, orderByClauses);
        int c = this.reader.read();
        if (c != -1) {
            String maybe = "";
            if (expression.getOrderByClauses().isEmpty()) {
                maybe = " Maybe a AND or OR operator is missing?";
            }
            unexpectedChar(c, "trying to parse the end of the CQL expression." + maybe);
        }

        return expression;
    }

    List<AQLOrderByClause> maybeParseOrderByClauses() throws ParserException, IOException
    {
        if (this.reader.readWord("order")) {
            eat("by");
            List<AQLOrderByClause> orderByClauses = new ArrayList<>();
            int c;
            do {
                this.reader.skipWhite();
                orderByClauses.add(parseOrderByClause());
                this.reader.skipWhite();
                c = this.reader.read();
            } while (c == ',');
            this.reader.unread(c);
            return orderByClauses;
        }
        return Collections.emptyList();
    }

    AQLOrderByClause parseOrderByClause() throws ParserException, IOException
    {
        AQLParserState state = this.reader.getState();
        String field = parseField();
        this.reader.skipWhite();
        boolean desc = false;
        if (this.reader.readWord("desc")) {
            desc = true;
        } else {
            this.reader.readWord("asc");
        }

        return new AQLOrderByClause(state, field, desc);
    }

    List<AQLClauseWithNextOperator> parseClausesWithNextOperator() throws ParserException, IOException
    {
        List<AQLClauseWithNextOperator> clauses = new ArrayList<>();
        AQLClauseWithNextOperator clause;
        do {
            clause = parseClauseWithNextOperator();
            clauses.add(clause);
        } while (clause.getNextOperator() != null);
        return clauses;
    }

    AQLClauseWithNextOperator parseClauseWithNextOperator() throws ParserException, IOException
    {
        AbstractAQLClause clause;
        this.reader.skipWhite();
        if (this.reader.maybeEat('(')) {
            clause = new AQLClausesWithNextOperator(this.reader.getState(), parseClausesWithNextOperator());
            this.reader.skipWhite();
            eatClosingParenthesis();
        } else {
            clause = parseAtom();
        }

        return new AQLClauseWithNextOperator(this.reader.getState(), clause, maybeParseClauseOperator());
    }

    private AQLAtomicClause parseAtom() throws ParserException, IOException
    {
        AQLParserState state = this.reader.getState();
        String field = parseField();
        this.reader.skipWhite();
        AQLAtomicClauseOperator op = parseAtomOperator();
        this.reader.skipWhite();
        AbstractAQLRightHandValue right = op.getOperator() == AQLOperator.IN || op.getOperator() == AQLOperator.NOT_IN
            ? parseInExpr()
            : parseAtomicValue(false);

        return new AQLAtomicClause(state, field, op, right);
    }

    private AQLInExpression parseInExpr() throws ParserException, IOException
    {
        AQLParserState state = reader.getState();
        List<AbstractAQLAtomicValue> values = new ArrayList<>();
        eatOpeningParenthesis();
        do {
            values.add(parseAtomicValue(true));
            this.reader.skipWhite();
        } while (this.reader.maybeEat(','));
        eatClosingParenthesis();
        return new AQLInExpression(state, values);
    }

    private AbstractAQLAtomicValue parseAtomicValue(boolean inParentheses) throws ParserException, IOException
    {
        this.reader.skipWhite();
        AQLParserState state = this.reader.getState();

        int c = this.reader.read();

        if (c == '"' || c == '\'') {
            return parseRemainingString(state, c);
        }

        if (Character.isDigit(c) || c == '.') {
            return parseRemainingNumberOrDate(c, state, this.reader);
        }

        if (Character.isAlphabetic(c)) {
            return parseRemainingAtomicValue(c, state);
        }

        this.reader.unread(c);
        if (inParentheses && c == ')') {
            error("Expected a value before closing parenthesis ')'");
        }

        // unreachable
        return null;
    }

    private AbstractAQLAtomicValue parseRemainingAtomicValue(int first, AQLParserState state)
        throws IOException, ParserException
    {
        StringBuilder identifier = new StringBuilder();
        int c = first;
        do {
            identifier.append((char) c);
            c = this.reader.read();
        } while (c == '_' || c == '.' || Character.isLetterOrDigit(c));

        if (Character.isWhitespace(c)) {
            this.reader.skipWhite();
            c = this.reader.read();
        }

        if (c == '(') {
            return new AQLFunctionCall(state, identifier.toString(), parseRemainingFunctionCallArguments());
        }

        this.reader.unread(c);

        String lower = identifier.toString().toLowerCase();
        if (TRUE.equals(lower) || FALSE.equals(lower)) {
            return new AQLBooleanLiteral(state, TRUE.equals(lower));
        }

        return new AQLStringLiteral(state, identifier.toString());
    }

    private List<AbstractAQLAtomicValue> parseRemainingFunctionCallArguments() throws ParserException, IOException
    {
        this.reader.skipWhite();
        if (this.reader.maybeEat(')')) {
            return Collections.emptyList();
        }

        List<AbstractAQLAtomicValue> arguments = new ArrayList<>();
        int c;
        do {
            arguments.add(parseAtomicValue(true));
            this.reader.skipWhite();
            c = this.reader.read();
        } while (c == ',');

        if (c != ')') {
            unexpectedChar(c, "trying to parse the end of the function call");
        }

        return arguments;
    }

    private static AbstractAQLAtomicValue parseRemainingNumberOrDate(int first, AQLParserState state, AQLReader r)
        throws IOException, ParserException
    {
        // In this method, we use the passed reader and not this.reader, because it can be used for trying to parse a
        // date in an already parsed string

        StringBuilder number = new StringBuilder();
        int c = first;
        if (c != '.' && c != 'e' && c != 'E') {
            do {
                number.append((char) c);
                c = r.read();
            } while (Character.isDigit(c));

            if (c == '-' || c == '/') {
                return parseRemainingDate((char) c, Integer.parseInt(number.toString()), state, r);
            }
        }

        boolean success = true;

        if (c == '.' || c == 'e' || c == 'E') {
            success = parseRemainingFloat(number, r, c);
            c = r.peek();
        } else {
            r.unread(c);
        }

        if (success && isEndChar(c)) {
            return new AQLNumberLiteral(state, number.toString());
        }

        // Weird character after the number, handling this as string.
        // FIXME: Not sure if the original AQL parser allows this.

        c = r.read();
        while (!isEndChar(c)) {
            number.append((char) c);
            c = r.read();
        }

        r.unread(c);

        return new AQLStringLiteral(state, number.toString());
    }

    private static boolean isEndChar(int c)
    {
        return c == -1 || Character.isWhitespace(c) || ",()".indexOf((char) c) != -1;
    }

    private static boolean parseRemainingFloat(StringBuilder number, AQLReader r, int first)
        throws IOException
    {
        int c = first;
        boolean ok = true;

        if (c == '.') {
            number.append('.');

            c = r.read();
            while (Character.isDigit(c)) {
                number.append((char) c);
                c = r.read();
            }
        }

        if (c == 'e' || c == 'E') {
            number.append((char) c);
            c = r.read();
            if (c == '-' || c == '+') {
                number.append((char) c);
                c = r.read();
            }

            // looking for an exponent after 'e'
            ok = false;
            while (Character.isDigit(c)) {
                ok = true;
                number.append((char) c);
                c = r.read();
            }
        }

        r.unread(c);
        return ok;
    }

    private static AQLDateLiteral parseRemainingDate(char dateSep, int year, AQLParserState state, AQLReader r)
        throws IOException, ParserException
    {
        int month = parseDatePart(dateSep, state, r, false);
        int day = parseDatePart(dateSep, state, r, true);
        int hours = -1;
        int minutes = -1;
        r.skipWhite();
        int c = r.peek();
        if (Character.isDigit(c)) {
            hours = parseDatePart(':', state, r, false);
            minutes = parseDatePart(-1, state, r, true);
        }

        // We don't check if the date is sensible (month between 1 and 12, days between 1 and 28, 29, 30, 31
        // depending on the month and the year, hours and minutes between 0 and 60).
        // Checking this might be a rabbit-hole and the Java Date constructor accepts all sorts of things, so
        // it's possible broken-looking date actually work in Confluence.
        // FIXME: To be checked.

        return new AQLDateLiteral(state, year, month, day, hours, minutes);
    }

    private static int parseDatePart(int dateSep, AQLParserState state, AQLReader r, boolean allowEnd)
        throws IOException, ParserException
    {
        StringBuilder atom = new StringBuilder();
        int c = r.read();
        while (Character.isDigit(c)) {
            atom.append((char) c);
            c = r.read();
        }

        if (atom.length() != 0 && (dateSep == -1 || (allowEnd && Character.isWhitespace(c)))) {
            r.unread(c);
        } else if (atom.length() == 0 || (c != dateSep && !(allowEnd && c == -1))) {
            // unreading this last character allows a more accurate error message positioning.
            r.unread(atom.length() == 0 ? c : atom.charAt(atom.length() - 1));
            error(r, "Expected '{}' in the date started at [{}]", (char) dateSep, state);
        }

        return Integer.parseInt(atom.toString());
    }

    private AbstractAQLAtomicValue parseRemainingString(AQLParserState state, int quote)
        throws IOException, ParserException
    {
        StringBuilder builder = new StringBuilder();
        while (true) {
            int c = this.reader.read();
            switch (c) {
                case -1:
                    error("Unexpected end, expected the end of string started at [{}]", state);
                    break;
                case '\\':
                    c = this.reader.read();
                    if (c == -1) {
                        error("Unexpected end, expected an escaped character after '\\', and the end of string "
                            + "started at [{}]", state);
                    }
                    break;
                case '"':
                case '\'':
                    if (c == quote) {
                        return parseEndString(state, builder);
                    }
                    break;
                default:
                    // ignore
            }
            builder.append((char) c);
        }
    }

    private AbstractAQLAtomicValue parseEndString(AQLParserState state, StringBuilder builder)
        throws IOException
    {
        String str = builder.toString();
        if (!str.isEmpty() && Character.isDigit(str.charAt(0))) {
            // Dates can be quoted, especially those which contain hours and minutes so let's try parsing
            // this string as date
            try {
                AQLReader r = new AQLReader(str.substring(1));
                AbstractAQLAtomicValue v = parseRemainingNumberOrDate(str.charAt(0), state, r);
                if (r.read() == -1 && v instanceof AQLDateLiteral) {
                    // Only return a date literal if the whole string has been read; parseRemainingNumberOrDate can
                    // return a number literal, and we don't want this instead of a string literal.
                    return v;
                }
            } catch (ParserException e) {
                // Well, this failed, so this is likely a regular string. Let's just produce a string literal.
            }
        }
        return new AQLStringLiteral(state, str);
    }

    private String parseField() throws IOException, ParserException
    {
        String field = this.reader.readAlphaNumericWord();
        if (this.reader.maybeEat('.')) {
            field += '.';
            field += parseField();
        }
        if (field.isEmpty()) {
            error("Expected a field");
        }
        return field.toLowerCase();
    }

    private AQLAtomicClauseOperator parseAtomOperator() throws ParserException, IOException
    {
        this.reader.skipWhite();

        AQLParserState state = this.reader.getState();

        if (this.reader.readWord(NOT)) {
            eat(IN);
            return new AQLAtomicClauseOperator(state, AQLOperator.NOT_IN);
        }

        if (this.reader.readWord(IN)) {
            return new AQLAtomicClauseOperator(state, AQLOperator.IN);
        }

        String op = this.reader.readWordOneOf(false, ATOM_OPS);
        if (op == null) {
            error("Expected a CQL operator among [IN, NOT IN, {}]", String.join(", ", ATOM_OPS));
        }

        return new AQLAtomicClauseOperator(state, ATOM_OPS_MAP.get(op));
    }

    private AQLClauseOperator maybeParseClauseOperator() throws ParserException, IOException
    {
        reader.skipWhite();
        AQLParserState state = this.reader.getState();
        String op = this.reader.readWordOneOf(true, AND, OR);
        if (op == null) {
            return null;
        }

        boolean isAnd = AND.equals(op);
        boolean isNot = this.reader.readWord(NOT);
        return new AQLClauseOperator(state, isAnd, isNot);
    }

    private void eat(String expectedWord) throws ParserException, IOException
    {
        this.reader.skipWhite();
        if (!this.reader.readWord(expectedWord, false)) {
            errorExpected(expectedWord);
        }
    }

    private void errorExpected(Object expectedCharOrWord) throws ParserException
    {
        error("Expected [{}]", expectedCharOrWord);
    }

    private void eatClosingParenthesis() throws ParserException, IOException
    {
        if (this.reader.read() != ')') {
            error("Expected closing parenthesis ')'");
        }
    }

    private void eatOpeningParenthesis() throws ParserException, IOException
    {
        if (this.reader.read() != '(') {
            error("Expected opening parenthesis '('");
        }
    }


    private void error(String message, Object... parameters) throws ParserException
    {
        error(this.reader, message, parameters);
    }

    private static void error(AQLReader reader, String message, Object... parameters) throws ParserException
    {
        AQLParserState state = reader.getState();

        error(state, message, parameters);
    }

    private static void error(AQLParserState state, String message, Object... parameters) throws ParserException
    {
        String msg = message;
        for (Object parameter : parameters) {
            msg = msg.replaceFirst("\\{}", parameter.toString());
        }
        throw new ParserException(msg, state);
    }

    private void unexpectedChar(int c, String msg) throws ParserException
    {
        if (c == -1) {
            error("Unexpected end while {}", msg);
        }
        error("Unexpected character '{}' while {}", (char) c, msg);
    }
}
