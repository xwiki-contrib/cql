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
package org.xwiki.contrib.cql.aqlparser.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.xwiki.contrib.cql.aqlparser.AQLParserState;
import org.xwiki.contrib.cql.aqlparser.internal.Parser;
import org.xwiki.stability.Unstable;

/**
 * Thrown when a parsing error happens.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class ParserException extends Exception
{
    private static final long serialVersionUID = 1L;

    private static final String PARSER_CLASS_NAME = Parser.class.getName();

    private static final Pattern PARSE_PREFIX_PATTERN = Pattern.compile("^(maybeParse|parse)(End|Remaining)?");

    private static final Pattern CAMEL_WORD_PATTERN = Pattern.compile("[A-Z]?[a-z]+");

    private final AQLParserState parserState;

    /**
     * @param message an explanation of the parse error
     * @param parserState the state corresponding to the point at which the error happens
     * @since 0.0.1
     */
    public ParserException(String message, AQLParserState parserState)
    {
        super(message + ' ' + '(' + parserState + ')');
        this.parserState = parserState;
    }

    /**
     * @param cause the wrapped exception.
     * @param parserState the state corresponding to the point at which the error happens
     * @since 0.0.1
     */
    public ParserException(Exception cause, AQLParserState parserState)
    {
        super("An unexpected error happened (" + parserState + ')', cause);
        this.parserState = parserState;
    }

    /**
     * @param message an explanation of the parse error
     * @param cause the wrapped Exception.
     * @param parserState the state corresponding to the point at which the error happens
     * @since 0.0.1
     */
    public ParserException(String message, Exception cause, AQLParserState parserState)
    {
        super(message + ' ' + '(' + parserState + ')', cause);
        this.parserState = parserState;
    }

    @Override
    public String getMessage()
    {
        String path = String.join(" > ", getPath());
        return super.getMessage() + (path.isEmpty() ? "" : ". Path: " + path);
    }

    /**
     * @return the user-friendly parsing path that lead to this exception.
     */
    private List<String> getPath()
    {
        StackTraceElement[] stackTrace = getStackTrace();
        List<String> path = new ArrayList<>(stackTrace.length);

        String lastElement = "";
        for (StackTraceElement line : stackTrace) {
            if (line.getClassName().equals(PARSER_CLASS_NAME)) {
                String method = line.getMethodName();
                String element = PARSE_PREFIX_PATTERN.matcher(method).replaceFirst("")
                    .replace("WithNextOperator", "");
                if (!element.isEmpty() && !method.equals(element) && !lastElement.equals(element)) {
                    path.add(0, camelToSpaces(element));
                    lastElement = element;
                }
            }
        }

        return path;
    }

    /**
     * @return the state of the parser of when the parse error happened.
     * @since 0.0.1
     */
    public AQLParserState getParserState()
    {
        return parserState;
    }

    private String camelToSpaces(String camel)
    {
        return CAMEL_WORD_PATTERN.matcher(camel).replaceAll("$0 ").trim().toLowerCase();
    }
}
