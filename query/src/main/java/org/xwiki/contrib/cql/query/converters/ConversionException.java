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
package org.xwiki.contrib.cql.query.converters;

import org.xwiki.contrib.cql.aqlparser.AQLParserState;
import org.xwiki.contrib.cql.aqlparser.exceptions.ParserException;

/**
 * Thrown when an error happens during CQL to Solr conversion.
 * @version $Id$
 * @since 0.0.1
 */

public class ConversionException extends ParserException
{
    private static final long serialVersionUID = 1L;
    /**
     * @param message an explanation of the conversion error
     * @param parserState the state corresponding to the point at which the error happens
     * @since 0.0.1
     */
    public ConversionException(String message, AQLParserState parserState)
    {
        super(message, parserState);
    }

    /**
     * @param message an explanation of the conversion error
     * @param cause the cause of the error
     * @param parserState the state corresponding to the point at which the error happens
     * @since 0.0.1
     */
    public ConversionException(String message, Exception cause, AQLParserState parserState)
    {
        super(message, cause, parserState);
    }

    /**
     * @param e The wrapped exception.
     * @param parserState the state corresponding to the point at which the error happens
     * @since 0.0.1
     */
    public ConversionException(Exception e, AQLParserState parserState)
    {
        super(e, parserState);
    }
}
