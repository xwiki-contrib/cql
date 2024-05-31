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
package org.xwiki.contrib.cql.aqlparser.ast;

import org.xwiki.contrib.cql.aqlparser.AQLParserState;
import org.xwiki.stability.Unstable;

/**
 * Represents a date literal.
 * Note: any quoted string that can be parsed as a CQL date will automatically produce a AQLDateLiteral instead of
 * producing a AQLStringLiteral.
 * @version $Id$
 * @since 0.0.1
 */
@Unstable
public class AQLDateLiteral extends AbstractAQLAtomicValue
{
    private final int year;

    private final int month;

    private final int day;

    private final int hours;

    private final int minutes;

    /**
     * @param parserState the state of the parser right before starting to parse this node
     * @param year the full (e.g. 4 digits for a date of the 21st century) year of the Current era (ISO era)
     * @param month the 1-indexed number of the month in the specified year (between 1 and 12, both included)
     * @param day the 1-indexed day of the month (between 1 and 31, both included)
     * @param hours the hours of the day between 0 and 23 (both included) if the date represents a moment
     *              or -1 if this date represents a day and not a moment.
     * @param minutes the minutes of the hour between 0 and 59 (both included) if the date represents a moment
     *                or -1 if this date represents a day and not a moment.
     * Note: minutes must be set to -1 if and only if hours is set to -1.
     * @since 0.0.1
     */
    public AQLDateLiteral(AQLParserState parserState, int year, int month, int day, int hours, int minutes)
    {
        super(parserState);
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
    }

    /**
     * @return the full (e.g. 4 digits for a date of the 21st century) year of the Current era (ISO era)
     * @since 0.0.1
     */
    public int getYear()
    {
        return year;
    }

    /**
     * @return the 1-indexed number of the month in the specified year (between 1 and 12, both included)
     * @since 0.0.1
     */
    public int getMonth()
    {
        return month;
    }

    /**
     * @return the 1-indexed day of the month (between 1 and 31, both included)
     * @since 0.0.1
     */
    public int getDay()
    {
        return day;
    }

    /**
     * @return the hours of the day between 0 and 23 (both included) if the date represents a moment
     *         or -1 if this date represents a day and not a moment.
     * @since 0.0.1
     */
    public int getHours()
    {
        return hours;
    }

    /**
     * @return the minutes of the hour between 0 and 59 (both included) if the date represents a moment
     *         or -1 if this date represents a day and not a moment.
     * @since 0.0.1
     */
    public int getMinutes()
    {
        return minutes;
    }
}
