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

import java.io.IOException;

import org.xwiki.contrib.cql.aqlparser.exceptions.ParserException;
import org.xwiki.contrib.cql.aqlparser.internal.Parser;
import org.xwiki.contrib.cql.aqlparser.ast.AQLStatement;
import org.xwiki.stability.Unstable;

/**
 * The AQL Parser.
 * It allows parsing CQL (Confluence Query Language) and most likely JQL (Jira Query Language) statements (both
 * language are very similar and likely to be implemented using the same parser in Confluence and Jira).
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public final class AQLParser
{
    private AQLParser()
    {
    }

    /**
     * Parse the AQL statement  passed as parameter.
     *
     * @param stmt the statement containing the AQL content to parse
     * @return the parsed result as an AST tree
     * @throws ParserException if a parse error happens
     * @throws IOException in case a IO error happens, though it shouldn't
     * @since 0.0.1
     */
    public static AQLStatement parse(String stmt) throws ParserException, IOException
    {
        return new Parser(stmt).parse();
    }
}
