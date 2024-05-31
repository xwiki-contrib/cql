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

import org.xwiki.stability.Unstable;

/**
 * The CQL operators that can appear inside an atomic clause.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public enum AQLOperator
{
    /**
     * The IN operator.
     */
    IN,

    /**
     * The NOT IN operator.
     */
    NOT_IN,

    /**
     * The equals ('=') operator.
     */
    EQ,

    /**
     * The not equals ('!=') operator.
     */
    NEQ,

    /**
     * The greater than ('&gt;') operator.
     */
    GT,

    /**
     * The greater than or equals ('&gt;=') operator.
     */
    GTE,

    /**
     * The lower than ('&lt;') operator.
     */
    LT,

    /**
     * The lower than or equals ('&lt;=') operator.
     */
    LTE,

    /**
     * The contains ('~') operator.
     */
    CONTAINS,

    /**
     * The does not contain ('!~') operator.
     */
    DOES_NOT_CONTAIN
}
