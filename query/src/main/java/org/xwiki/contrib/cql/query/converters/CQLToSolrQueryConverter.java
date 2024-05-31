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

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.cql.aqlparser.ast.AQLClauseWithNextOperator;
import org.xwiki.contrib.cql.aqlparser.ast.AQLOrderByClause;
import org.xwiki.contrib.cql.aqlparser.ast.AQLStatement;
import org.xwiki.contrib.cql.aqlparser.ast.AbstractAQLClause;
import org.xwiki.contrib.cql.aqlparser.ast.AQLAtomicClause;
import org.xwiki.contrib.cql.aqlparser.ast.AQLClauseOperator;
import org.xwiki.contrib.cql.aqlparser.ast.AQLClausesWithNextOperator;
import org.xwiki.stability.Unstable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.xwiki.contrib.cql.query.converters.Utils.betweenParentheses;

/**
 * Translates a CQL query to a Solr Query.
 * @version $Id$
 * @since 0.0.1
 */
@Component (roles = CQLToSolrQueryConverter.class)
@Singleton
@Unstable
public class CQLToSolrQueryConverter
{
    private static final String UNEXP = " This is unexpected, please report an issue";

    @Inject
    @Named("")
    private CQLToSolrAtomConverter atomConverter;

    @Inject
    private CQLToSolrSortFieldConverter sortFieldConverter;

    @Inject
    private ComponentManager componentManager;

    /**
     * @return the Solr sort parameter of the given cql statement
     * @param cql the cql statement
     * @throws ConversionException if something wrong happens
     */
    public String getSolrSortParameter(AQLStatement cql)
        throws ConversionException
    {
        StringBuilder res = new StringBuilder();

        for (AQLOrderByClause orderByParameter : cql.getOrderByClauses()) {
            String cqlField = orderByParameter.getField();
            String solrSortParameter = getSolrSortParameter(cql, orderByParameter, cqlField);

            if (res.length() > 0) {
                res.append(',');
            }
            res.append(solrSortParameter);
        }

        return res.toString().trim();
    }

    private String getSolrSortParameter(AQLStatement cql, AQLOrderByClause orderByParameter, String cqlField)
        throws ConversionException
    {
        String solrSortParameter = null;
        List<Object> converters;

        try {
            converters = componentManager.getInstanceList(CQLToSolrSortFieldConverter.class);
        } catch (ComponentLookupException e) {
            throw new ConversionException(e, orderByParameter.getParserState());
        }

        for (Object converterObj : converters) {
            CQLToSolrSortFieldConverter converter = (CQLToSolrSortFieldConverter) converterObj;

            if (converter != sortFieldConverter) {
                solrSortParameter = converter.getSolrSortParameter(cql, orderByParameter, cqlField);
                if (solrSortParameter != null && !solrSortParameter.isEmpty()) {
                    break;
                }
            }
        }

        if (solrSortParameter == null || solrSortParameter.isEmpty()) {
            solrSortParameter = sortFieldConverter.getSolrSortParameter(cql, orderByParameter, cqlField);
        }

        if (solrSortParameter == null || solrSortParameter.isEmpty()) {
            throw new ConversionException(String.format("Ordering by field [%s] is not supported", cqlField),
                orderByParameter.getParserState());
        }
        return solrSortParameter;
    }

    /**
     * @return the corresponding Solr statement
     * @param cql the cql statement
     * @throws ConversionException if something wrong happens
     */
    public String getSolrStatement(AQLStatement cql)
        throws ConversionException
    {
        return convertToSolr(cql);
    }

    private String convertToSolr(AQLStatement expression) throws ConversionException
    {
        return convertToSolr(expression.getClausesWithNextOp());
    }

    private String convertToSolr(List<AQLClauseWithNextOperator> clausesWithNextOp)
        throws ConversionException
    {
        if (clausesWithNextOp.size() == 1) {
            return convertToSolr(clausesWithNextOp.get(0));
        }

        StringBuilder solrQuery = new StringBuilder();
        for (AQLClauseWithNextOperator clauseWithNextOp : clausesWithNextOp) {
            String solrClause = convertToSolr(clauseWithNextOp);
            solrQuery.append(betweenParentheses(solrClause));
            AQLClauseOperator nextOp = clauseWithNextOp.getNextOperator();
            if (nextOp != null) {
                solrQuery.append(nextOp.isAnd() ? " AND " : " OR ").append(nextOp.isNot() ? "-" : "");
            }
        }

        return solrQuery.toString().trim();
    }

    private String convertToSolr(AQLClauseWithNextOperator clauseWithNextOp) throws ConversionException
    {
        String solrClause = null;
        AbstractAQLClause clause = clauseWithNextOp.getClause();
        if (clause instanceof AQLAtomicClause) {
            solrClause = convertToSolr((AQLAtomicClause) clause);
        } else if (clause instanceof AQLClausesWithNextOperator) {
            solrClause =
                convertToSolr(((AQLClausesWithNextOperator) clause).getClausesWithNextOp());
        }

        if (solrClause == null || solrClause.isEmpty()) {
            throw new ConversionException("BUG: Failed to convert this clause." + UNEXP, clause.getParserState());
        }

        return solrClause;
    }

    private String convertToSolr(AQLAtomicClause atom) throws ConversionException
    {
        String result = null;

        // Find a specialized converter for this field and use it if any.
        String lowerField = atom.getField().toLowerCase();
        if (this.componentManager.hasComponent(CQLToSolrAtomConverter.class, lowerField)) {
            CQLToSolrAtomConverter converter;
            try {
                converter = this.componentManager.getInstance(CQLToSolrAtomConverter.class, lowerField);
            } catch (ComponentLookupException e) {
                throw new ConversionException(e, atom.getParserState());
            }
            result = converter.convertToSolr(atom);
        }

        return result == null ? atomConverter.convertToSolr(atom) : result;
    }
}
