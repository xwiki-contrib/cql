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
package org.xwiki.contrib.cql.query.internal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.cql.aqlparser.AQLParser;
import org.xwiki.contrib.cql.aqlparser.ast.AQLStatement;
import org.xwiki.contrib.cql.query.converters.CQLToSolrQueryConverter;
import org.xwiki.contrib.cql.aqlparser.exceptions.ParserException;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryExecutor;
import org.xwiki.query.QueryExecutorManager;
import org.xwiki.query.WrappingQuery;
import org.xwiki.stability.Unstable;

import com.xpn.xwiki.XWikiContext;

/**
 * Executes CQL queries.
 * Like org.xwiki.query.solr.internal.SolrQueryExecutor, the result is the direct QueryResponse.
 *
 * @version $Id$
 * @since 0.0.1
 */
@Component
@Named(CQLQueryExecutor.CQL)
@Singleton
@Unstable
public class CQLQueryExecutor implements QueryExecutor
{
    /**
     * Query language ID.
     */
    public static final String CQL = "cql";

    private static final String SORT = "sort";

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private QueryExecutorManager queryExecutorManager;

    @Inject
    private CQLToSolrQueryConverter queryConverter;

    @Inject
    private Logger logger;

    @Override
    public <T> List<T> execute(Query query) throws QueryException
    {
        String solrStatement;
        String solrSortParameter;
        String cql = query.getStatement();
        try {
            AQLStatement cqlAst = AQLParser.parse(cql);
            solrStatement = queryConverter.getSolrStatement(cqlAst);
            solrSortParameter = queryConverter.getSolrSortParameter(cqlAst);
        } catch (ParserException | IOException e) {
            throw new QueryException("Failed to parse the CQL statement", query, e);
        }

        this.logger.debug("CQL Statement [{}] converted to Solr query [{}], sort parameter [{}] for execution",
            cql, solrStatement, solrSortParameter);

        return wrapAsSolrQuery(query, solrStatement, solrSortParameter).execute();
    }

    private Query wrapAsSolrQuery(Query query, String solrStatement, String solrSortParameter)
    {
        Query q = new CQLToSolrWrappingQuery(query, solrStatement);
        Object existingSortParameter = q.getNamedParameters().get(SORT);
        boolean sortParameterUnset = existingSortParameter == null || (existingSortParameter instanceof String
            && ((String) existingSortParameter).isEmpty());

        if (sortParameterUnset && solrSortParameter != null && !solrSortParameter.isEmpty()) {
            // We only set the sort parameter according to the CQL expression if it's not empty and a sort parameter
            // has not already been provided by the callee.
            // Said differently, the callee has priority over the CQL statement and can override its order by clause.
            q.bindValue(SORT, solrSortParameter);
        }
        return q;
    }

    private class CQLToSolrWrappingQuery extends WrappingQuery
    {
        private final String solrStatement;

        CQLToSolrWrappingQuery(Query query, String solrStatement)
        {
            super(query);
            this.solrStatement = solrStatement;
        }

        @Override
        public String getLanguage()
        {
            return "solr";
        }

        @Override
        public String getStatement()
        {
            return solrStatement;
        }

        @Override
        public <T> List<T> execute() throws QueryException
        {
            return queryExecutorManager.execute(this);
        }

        // Workaround for https://jira.xwiki.org/browse/XWIKI-22176

        @Override
        public Query bindValue(String p, Object val)
        {
            getWrappedQuery().bindValue(p, val);
            return this;
        }

        @Override
        public Query bindValue(int index, Object val)
        {
            getWrappedQuery().bindValue(index, val);
            return this;
        }

        @Override
        public Query bindValues(List<Object> values)
        {
            getWrappedQuery().bindValues(values);
            return this;
        }

        @Override
        public Query bindValues(Map<String, ?> values)
        {
            getWrappedQuery().bindValues(values);
            return this;
        }
    }
}
