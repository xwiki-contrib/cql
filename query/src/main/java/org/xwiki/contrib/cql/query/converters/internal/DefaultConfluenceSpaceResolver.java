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
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.cql.aqlparser.ast.AbstractAQLRightHandValue;
import org.xwiki.contrib.cql.query.converters.ConfluenceSpaceResolver;
import org.xwiki.contrib.cql.query.converters.ConversionException;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.stability.Unstable;

import com.xpn.xwiki.XWikiContext;

/**
 * Default ConfluenceSpaceResolver, using the available implementations.
 * @version $Id$
 * @since 0.0.1
 */
@Component
@Unstable
@Singleton
@Priority(900)
public class DefaultConfluenceSpaceResolver implements ConfluenceSpaceResolver
{
    @Inject
    private ComponentManager componentManager;

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private Logger logger;

    @Override
    public EntityReference getSpaceByKey(AbstractAQLRightHandValue node, String spaceKey) throws ConversionException
    {
        for (ConfluenceSpaceResolver spaceResolver : getConfluenceSpaceResolvers(node)) {
            if (spaceResolver != this) {
                EntityReference docRef = spaceResolver.getSpaceByKey(node, spaceKey);
                if (docRef != null) {
                    logger.debug("Confluence space [{}] resolved to [{}] using [{}]", spaceKey, docRef, spaceResolver);
                    return docRef;
                }
            }
        }

        // Fallback.
        // FIXME this is a bit optimistic. We need to make this work with Confluence spaces migrated in a (non-empty)
        // root space.
        EntityReference docRef = new EntityReference(spaceKey, EntityType.SPACE);
        logger.debug("Confluence space [{}] resolved to [{}] using fallback", spaceKey, docRef);
        return docRef;
    }

    @Override
    public EntityReference getCurrentConfluenceSpace(AbstractAQLRightHandValue node) throws ConversionException
    {
        for (ConfluenceSpaceResolver spaceResolver : getConfluenceSpaceResolvers(node)) {
            if (spaceResolver != this) {
                EntityReference docRef = spaceResolver.getCurrentConfluenceSpace(node);
                if (docRef != null) {
                    logger.debug("Current space resolved to [{}] using [{}]", docRef, spaceResolver);
                    return docRef;
                }
            }
        }

        // Fallback.
        // FIXME this is a bit optimistic. We need to make this work with Confluence spaces migrated in a
        //  (non-empty) root space
        EntityReference spaceEntity = contextProvider.get().getDoc().getDocumentReference();
        while (spaceEntity.getParent() != null && spaceEntity.getParent().getType().equals(EntityType.SPACE)) {
            spaceEntity = spaceEntity.getParent();
        }

        logger.debug("Current space resolved to [{}] using fallback", spaceEntity);
        return spaceEntity;
    }

    private List<ConfluenceSpaceResolver> getConfluenceSpaceResolvers(AbstractAQLRightHandValue node)
        throws ConversionException
    {
        List<ConfluenceSpaceResolver> resolvers;

        try {
            resolvers = componentManager.getInstanceList(ConfluenceSpaceResolver.class);
        } catch (ComponentLookupException e) {
            throw new ConversionException(e, node == null ? null : node.getParserState());
        }
        return resolvers;
    }
}
