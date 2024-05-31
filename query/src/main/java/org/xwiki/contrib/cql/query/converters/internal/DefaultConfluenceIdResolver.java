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
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.cql.aqlparser.AQLParserState;
import org.xwiki.contrib.cql.aqlparser.ast.AbstractAQLRightHandValue;
import org.xwiki.contrib.cql.query.converters.ConfluenceIdResolver;
import org.xwiki.contrib.cql.query.converters.ConversionException;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.stability.Unstable;

/**
 * Default ConfluenceIdResolver, using the available implementations.
 * @version $Id$
 * @since 0.0.1
 */
@Component
@Unstable
@Singleton
@Priority(900)
public class DefaultConfluenceIdResolver implements ConfluenceIdResolver
{
    @Inject
    private ComponentManager componentManager;

    @Inject
    private Logger logger;

    @Override
    public EntityReference getDocumentById(AbstractAQLRightHandValue node, long id) throws ConversionException
    {
        List<ConfluenceIdResolver> resolvers;

        AQLParserState state = node == null ? null : node.getParserState();

        try {
            resolvers = componentManager.getInstanceList(ConfluenceIdResolver.class);
        } catch (ComponentLookupException e) {
            throw new ConversionException(e, state);
        }

        for (ConfluenceIdResolver idResolver : resolvers) {
            if (idResolver != this) {
                EntityReference docRef = idResolver.getDocumentById(node, id);
                if (docRef != null) {
                    logger.debug("Confluence document id [{}] resolved to [{}] using [{}]", id, docRef, idResolver);
                    return docRef;
                }
            }
        }

        throw new ConversionException(String.format("Could not find the document matching Confluence id [%d]", id),
            state);
    }
}
