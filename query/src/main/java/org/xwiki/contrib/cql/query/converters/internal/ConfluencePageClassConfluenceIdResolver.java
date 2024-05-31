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

import java.util.List;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.cql.aqlparser.ast.AbstractAQLRightHandValue;
import org.xwiki.contrib.cql.query.converters.ConfluenceIdResolver;
import org.xwiki.contrib.cql.query.converters.ConversionException;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

import com.xpn.xwiki.doc.XWikiDocument;

import static org.xwiki.query.Query.HQL;

/**
 * Attempts to find a document from its confluence ID using Confluence.Code.ConfluencePageClass objects optionally
 * pushed by the confluence-xml package.
 * @since 0.0.1
 * @version $Id$
 */
@Component
@Named("confluencepageclass")
@Singleton
@Priority(900)
public class ConfluencePageClassConfluenceIdResolver implements ConfluenceIdResolver
{
    // The following HQL statement was translated from the following XWQL statement:
    // ---
    // select doc from Document doc, doc.object(Confluence.Code.ConfluencePageClass) o where o.id = :id
    // ---
    // This is because XWQL requires Confluence.Code.ConfluencePageClass to be present in the wiki
    // while the translated HQL does not. This makes the query a little bit more robust.

    private static final String ID_USING_CONFLUENCEPAGECLASS = "select doc "
        + "from XWikiDocument doc, BaseObject o, LongProperty idProp "
        + "where "
        + "idProp.value = :id and "
        + "doc.fullName = o.name and "
        + "o.className = 'Confluence.Code.ConfluencePageClass' and "
        + "idProp.id.id = o.id and "
        + "idProp.id.name = 'id'";

    @Inject
    private QueryManager queryManager;

    @Inject
    private Logger logger;

    @Override
    public EntityReference getDocumentById(AbstractAQLRightHandValue node, long id) throws ConversionException
    {
        List<Object> results;
        try {
            results = queryManager.createQuery(ID_USING_CONFLUENCEPAGECLASS, HQL)
                .bindValue("id", id)
                .setLimit(1)
                .execute();
        } catch (QueryException e) {
            throw new ConversionException(e, node == null ? null : node.getParserState());
        }

        if (results.isEmpty()) {
            return null;
        }

        return ((XWikiDocument) results.get(0)).getDocumentReference();
    }
}
