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

import org.apache.commons.lang3.StringUtils;

/**
 * Utils methods.
 * @since 0.0.1
 * @version $Id$
 */
public final class Utils
{
    private static final String[] SOLR_SPECIAL_CHARS = new String[] {
        "+", "-", "&amp;&amp;", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":", "/", "\\", " ",
        "AND", "OR", "NOT"};

    private static final String[] ESCAPED_SOLR_SPECIAL_CHARS = new String[] {
        "\\+", "\\-", "\\&amp;&amp;", "\\||", "\\!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\\\"", "\\~",
        "\\*", "\\?", "\\:", "\\/", "\\\\", "\\ ", "\\AND", "\\OR", "\\NOT"};

    private Utils()
    {

    }

    /**
     * @return the escaped value to use in a Solr standard query.
     * @param v the value to escape
     * @since 0.0.1
     *
     * NOTE: Likely to be removed when XCOMMONS-2926 is solved.
     */
    public static String escapeSolr(String v)
    {
        if (v.isEmpty()) {
            return "\"\"";
        }

        return StringUtils.replaceEach(v, SOLR_SPECIAL_CHARS, ESCAPED_SOLR_SPECIAL_CHARS);
    }

    /**
     * @param v the value to put into parentheses
     * @return v, between parentheses
     * @since 0.0.1
     */
    public static String betweenParentheses(String v)
    {
        return '(' + v + ')';
    }
}
