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
package org.xwiki.contrib.cql.aqlparser.internal;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import org.xwiki.contrib.cql.aqlparser.AQLParserState;
import org.xwiki.contrib.cql.aqlparser.exceptions.ParserException;

/**
 * @version $Id$
 * @since 0.0.1
 */
class AQLReader extends PushbackReader
{
    private long pos;

    private long line = 1;

    private long previousCol = 1;

    private long col = 1;

    AQLReader(String s)
    {
        super(new StringReader(s), s.length());
    }

    @Override
    public int read() throws IOException
    {
        int c = super.read();
        pos++;
        if (c == '\n') {
            line++;
            previousCol = col;
            col = 1;
        } else {
            col++;
        }

        return c;
    }

    @Override
    public void unread(int c) throws IOException
    {
        if (c == -1) {
            return;
        }

        super.unread(c);
        if (c == '\n') {
            line--;
            col = previousCol;
        } else {
            col--;
        }
        pos--;
    }

    @Override
    public void unread(char[] cbuf, int off, int len) throws IOException
    {
        for (int i = off + len - 1; i >= off; i--) {
            unread(cbuf[i]);
        }
    }

    public int peek() throws IOException
    {
        int c = read();
        unread(c);
        return c;
    }

    public String readWordOneOf(boolean requireWhiteOrEndAfter, String... words) throws IOException, ParserException
    {
        skipWhite();
        for (String word : words) {
            if (readWord(word, requireWhiteOrEndAfter)) {
                return word;
            }
        }

        return null;
    }

    public boolean readWord(String word) throws IOException, ParserException
    {
        return readWord(word, true);
    }

    public boolean readWord(String word, boolean requireWhiteOrEndAfter) throws IOException, ParserException
    {
        return readWord(word, requireWhiteOrEndAfter, ",(");
    }

    public boolean readWord(String word, boolean requireWhiteOrEndAfter, String endChars)
        throws IOException, ParserException
    {
        skipWhite();
        for (int i = 0, n = word.length(); i < n; i++) {
            char expectedChar = word.charAt(i);
            if (Character.isWhitespace(expectedChar)) {
                throw new ParserException(
                    "readWord can't be called with whitespace characters, please use a call per word.", getState());
            }
            int actualChar = read();
            if (Character.toLowerCase(expectedChar) != Character.toLowerCase(actualChar)) {
                unread(actualChar);
                for (int j = i - 1; j >= 0; j--) {
                    unread(word.charAt(j));
                }
                return false;
            }
        }

        if (!requireWhiteOrEndAfter || isNextCharWhiteOrEnd(endChars)) {
            return true;
        }

        unread(word.toCharArray());
        return false;
    }

    public String readAlphaNumericWord() throws IOException
    {
        StringBuilder word = new StringBuilder();
        int c = read();
        if (!Character.isAlphabetic(c)) {
            unread(c);
            return "";
        }
        word.append((char) c);

        while (true) {
            c = read();
            if (!Character.isLetterOrDigit(c)) {
                unread(c);
                break;
            }
            word.append((char) c);
        }
        return word.toString();
    }

    // FIXME check that unread(array) works well with pos, line, cur

    public void skipWhite() throws IOException
    {
        int c = read();
        if (Character.isWhitespace(c)) {
            skipWhite();
        } else {
            unread(c);
        }
    }

    public boolean isNextCharWhiteOrEnd(String endChars) throws IOException
    {
        int c = read();
        unread(c);
        return c == -1 || Character.isWhitespace(c) || (endChars != null && endChars.indexOf(c) != -1);
    }

    public boolean maybeEat(char expectedChar) throws IOException
    {
        int c = read();
        if (c == expectedChar) {
            return true;
        }

        unread(c);
        return false;
    }

    AQLParserState getState()
    {
        return new AQLParserState(pos, line, col);
    }
}
