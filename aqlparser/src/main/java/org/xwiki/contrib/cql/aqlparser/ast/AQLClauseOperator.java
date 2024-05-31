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
 * Represents an operator between two clauses. Possibilities: AND, OR, AND NOT, OR NOT, fully coded with two booleans.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class AQLClauseOperator extends AbstractAQLNode
{
    private final boolean isAnd;

    private final boolean isNot;

    /**
     * @param parserState the state of the parser right before starting to parse this node
     * @param isAnd whether the operator is "and" or "and not"
     * @param isNot whether the operator is "or not" or "and not"
     * @since 0.0.1
     */
    public AQLClauseOperator(AQLParserState parserState, boolean isAnd, boolean isNot)
    {
        super(parserState);
        this.isAnd = isAnd;
        this.isNot = isNot;
    }

    /**
     * @return whether the operator is "and" or "and not"
     * @since 0.0.1
     */
    public boolean isAnd()
    {
        return isAnd;
    }

    /**
     * @return whether the operator is "or not" or "and not"
     * @since 0.0.1
     */
    public boolean isNot()
    {
        return isNot;
    }
}
