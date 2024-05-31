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
package org.xwiki.contrib.cql.aqlparser.ast;

import org.xwiki.contrib.cql.aqlparser.AQLParserState;
import org.xwiki.stability.Unstable;

/**
 * Represents an atomic clause. This is a clause that doesn't contain sub-close, but an actual expression with a
 * (left-hand side) field, an operator and a right-hand side value.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class AQLAtomicClause extends AbstractAQLClause
{
    private final String field;

    private final AQLAtomicClauseOperator op;

    private final AbstractAQLRightHandValue right;

    /**
     * @param parserState the state of the parser right before starting to parse this node
     * @param field the left and side field to which the clause applies
     * @param op the operator between the field and the right-hand side value
     * @param right the right-hand side value (a literal, or what follows an IN operator)
     * @since 0.0.1
     */
    public AQLAtomicClause(AQLParserState parserState, String field, AQLAtomicClauseOperator op,
        AbstractAQLRightHandValue right)
    {
        super(parserState);
        this.field = field;
        this.op = op;
        this.right = right;
    }

    /**
     * @return the field
     * @since 0.0.1
     */
    public String getField()
    {
        return field;
    }

    /**
     * @return the operator
     * @since 0.0.1
     */
    public AQLAtomicClauseOperator getOp()
    {
        return op;
    }

    /**
     * @return the right-hand side value (a literal, or what follows an IN operator).
     * @since 0.0.1
     */
    public AbstractAQLRightHandValue getRight()
    {
        return right;
    }
}
