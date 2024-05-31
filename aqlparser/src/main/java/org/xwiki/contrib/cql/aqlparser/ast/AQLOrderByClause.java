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
 * Represents an order by close (a field and an ordering).
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class AQLOrderByClause extends AbstractAQLNode
{
    private final String field;

    private final boolean desc;

    /**
     * @param parserState the state of the parser right before starting to parse this node
     * @param field the field name
     * @param desc whether the order should be reversed
     * @since 0.0.1
     */
    public AQLOrderByClause(AQLParserState parserState, String field, boolean desc)
    {
        super(parserState);
        this.field = field;
        this.desc = desc;
    }

    /**
     * @return the field name
     * @since 0.0.1
     */
    public String getField()
    {
        return field;
    }

    /**
     * @return whether the order should be reversed
     * @since 0.0.1
     */
    public boolean isDesc()
    {
        return desc;
    }
}
