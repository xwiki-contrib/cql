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

import java.util.List;

import org.xwiki.contrib.cql.aqlparser.AQLParserState;
import org.xwiki.stability.Unstable;

/**
 * Represents a CQL statement, with clauses and order by clauses. This is the root AST node produced by the CQL parser.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class AQLStatement extends AQLClausesWithNextOperator
{
    private final List<AQLOrderByClause> orderByClauses;

    /**
     * @param parserState the state of the reader right before starting to parse this statement (likely at position 0,
     * line 1, column 1)
     * @param clausesWithNextOp the clauses
     * @param orderByClauses the order by clauses, from left to right
     * @since 0.0.1
     */
    public AQLStatement(AQLParserState parserState, List<AQLClauseWithNextOperator> clausesWithNextOp,
        List<AQLOrderByClause> orderByClauses)
    {
        super(parserState, clausesWithNextOp);
        this.orderByClauses = orderByClauses;
    }

    /**
     * @return the order by clauses
     * @since 0.0.1
     */
    public List<AQLOrderByClause> getOrderByClauses()
    {
        return orderByClauses;
    }
}
