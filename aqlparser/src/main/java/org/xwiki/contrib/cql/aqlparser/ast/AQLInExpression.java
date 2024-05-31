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
 * Represents an IN expression. This is what follows an IN or a NOT IN operator in an atomic clause, in parentheses.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class AQLInExpression extends AbstractAQLRightHandValue
{
    private final List<AbstractAQLAtomicValue> values;

    /**
     * @param parserState the state of the parser right before starting to parse this node
     * @param values the list of atomic values in this IN expression.
     * @since 0.0.1
     */
    public AQLInExpression(AQLParserState parserState, List<AbstractAQLAtomicValue> values)
    {
        super(parserState);
        this.values = values;
    }

    /**
     * @return the list of atomic values in this in expression.
     * @since 0.0.1
     */
    public List<AbstractAQLAtomicValue> getValues()
    {
        return values;
    }
}
