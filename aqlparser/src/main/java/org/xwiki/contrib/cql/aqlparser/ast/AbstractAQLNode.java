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
 * Represents a CQL AST node, produced when parsing a CQL statement.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public abstract  class AbstractAQLNode
{
    private final AQLParserState parserState;

    /**
     * @param parserState the state of the parser right before starting to parse this node
     * @since 0.0.1
     */
    AbstractAQLNode(AQLParserState parserState)
    {
        this.parserState = parserState;
    }

    /**
     * @return the state in which the reader was right before parsing this node
     * @since 0.0.1
     */
    public AQLParserState getParserState()
    {
        return parserState;
    }
}
