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
 * Represents a clause with the operator which is between this clause and the next one.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class AQLClauseWithNextOperator extends AbstractAQLNode
{
    private final AbstractAQLClause clause;

    private final AQLClauseOperator nextOperator;

    /**
     * @param parserState the state of the parser right before starting to parse this node
     * @param clause the clause
     * @param nextOp the operator between this clause and the next one. Null for the last clause in the containing list.
     * @since 0.0.1
     */
    public AQLClauseWithNextOperator(AQLParserState parserState, AbstractAQLClause clause, AQLClauseOperator nextOp)
    {
        super(parserState);
        this.clause = clause;
        this.nextOperator = nextOp;
    }

    /**
     * @return the clause
     * @since 0.0.1
     */
    public AbstractAQLClause getClause()
    {
        return clause;
    }

    /**
     * @return the operator between this clause and the next one. Null for the last clause in the containing list.
     * @since 0.0.1
     */
    public AQLClauseOperator getNextOperator()
    {
        return nextOperator;
    }
}
