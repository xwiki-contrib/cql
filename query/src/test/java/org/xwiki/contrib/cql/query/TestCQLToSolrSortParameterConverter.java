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
package org.xwiki.contrib.cql.query;

import javax.inject.Named;

import org.xwiki.contrib.cql.aqlparser.ast.AQLOrderByClause;
import org.xwiki.contrib.cql.aqlparser.ast.AQLStatement;
import org.xwiki.contrib.cql.query.converters.CQLToSolrSortFieldConverter;
import org.xwiki.contrib.cql.query.converters.DefaultCQLToSolrSortParameterConverter;

/**
 * Test CQL To Solr Sort Field Converter.
 * @version $Id$
 */
@Named("test3")
public class TestCQLToSolrSortParameterConverter extends DefaultCQLToSolrSortParameterConverter
    implements CQLToSolrSortFieldConverter
{
    @Override
    public String getSolrSortField(AQLStatement cql, AQLOrderByClause orderByParameter, String field)
    {
        if (field.equals("test3")) {
            return "test_sort";
        }

        return null;
    }
}
