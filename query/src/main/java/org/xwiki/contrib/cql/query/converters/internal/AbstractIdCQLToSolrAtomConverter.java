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
package org.xwiki.contrib.cql.query.converters.internal;

import javax.inject.Inject;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.cql.aqlparser.ast.AbstractAQLAtomicValue;
import org.xwiki.contrib.cql.aqlparser.ast.AQLAtomicClause;
import org.xwiki.contrib.cql.query.converters.ConfluenceIdResolver;
import org.xwiki.contrib.cql.query.converters.ConversionException;
import org.xwiki.contrib.cql.query.converters.DefaultCQLToSolrAtomConverter;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.stability.Unstable;

import static org.xwiki.contrib.cql.query.converters.Utils.escapeSolr;

/**
 * A helper class to implement CQL fields which deal with Confluence IDs.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
@Component(staticRegistration = false)
public abstract class AbstractIdCQLToSolrAtomConverter extends DefaultCQLToSolrAtomConverter
{
    @Inject
    private ConfluenceIdResolver idResolver;

    @Override
    protected String convertToSolr(AQLAtomicClause atom, AbstractAQLAtomicValue right) throws ConversionException
    {
        String value = super.convertToSolr(atom, right);

        long id;
        try {
            id = Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ConversionException("Expected a Confluence content id (a number)", right.getParserState());
        }

        EntityReference docRef = idResolver.getDocumentById(atom.getRight(), id);
        if (docRef == null) {
            return null;
        }

        String v = getValue(docRef);
        if (v == null) {
            return null;
        }

        return escapeSolr(v);
    }

    protected abstract String getValue(EntityReference docRef);
}
