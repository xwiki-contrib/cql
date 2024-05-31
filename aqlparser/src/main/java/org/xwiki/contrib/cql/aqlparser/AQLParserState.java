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
package org.xwiki.contrib.cql.aqlparser;

import java.io.Serializable;

import org.xwiki.stability.Unstable;

/**
 * Represents the state of the reader at a given point during the parsing of an CQL statement.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class AQLParserState implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final long pos;

    private final long line;

    private final long col;

    /**
     *
     * @param pos The 0-indexed position, in characters, in the CQL string.
     * @param line The (1-indexed) line number in the CQL string.
     * @param col The (1-indexed) column position the CQL string (in the line whose number is returned by #getLine()).
     * @since 0.0.1
     */
    public AQLParserState(long pos, long line, long col)
    {
        this.pos = pos;
        this.line = line;
        this.col = col;
    }

    /**
     * @return The 0-indexed position, in characters, in the CQL string.
     * @since 0.0.1
     */
    public long getPos()
    {
        return pos;
    }

    /**
     * @return The (1-indexed) line number in the CQL string.
     * @since 0.0.1
     */
    public long getLine()
    {
        return line;
    }

    /**
     * @return The (1-indexed) column position the CQL string (in the line whose number is returned by #getLine()).
     * @since 0.0.1
     */
    public long getCol()
    {
        return col;
    }

    @Override
    public String toString()
    {
        return String.format("line %d, col %d, pos %d", getLine(), getCol(), getPos());
    }
}
