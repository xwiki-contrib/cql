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

import org.xwiki.component.annotation.Role;
import org.xwiki.contrib.cql.aqlparser.ast.AQLOrderByClause;
import org.xwiki.contrib.cql.aqlparser.ast.AQLStatement;
import org.xwiki.stability.Unstable;

/**
 * CQL To Solr Sort Field Converter.
 * @since 0.0.1
 * @version $Id$
 */
@Role
@Unstable
public interface CQLToSolrSortFieldConverter
{
    /**
     * @param cql the CQL statement in which the field appears
     * @param orderByParameter the order by parameter being handled
     * @param field the field to convert
     * @return the corresponding sort parameter in solr (e.g. "field desc", or "field1 desc,field2 asc")
     */
    String getSolrSortParameter(AQLStatement cql, AQLOrderByClause orderByParameter, String field)
        throws ConversionException;
}
