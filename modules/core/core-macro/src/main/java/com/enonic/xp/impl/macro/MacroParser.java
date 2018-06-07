package com.enonic.xp.impl.macro;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.macro.Macro;

/**
 * Parse macro strings.
 * Format:
 * <ul>
 * <li>[macroname attrib1="text with \"escaped quotes\"." attrib2="value2"] body [/macroname]
 * <li>[macroname] body [/macroname]
 * <li>[macroname/]
 * </ul>
 */
public final class MacroParser
{
    private enum Token
    {
        LEFT_BRACKET,
        NAME,
        QUOTE,
        EQUALS,
        SLASH,
        RIGHT_BRACKET,
    }

    private static final char EOF = (char) -1;

    private String input;

    private int p;

    private char c;

    private String macroName;

    private final Multimap<String, String> attributes = ArrayListMultimap.create();

    private String body;

    private boolean debugMode = false;

    public Macro parse( final String text )
    {
        return parse( text, 0 );
    }

    public Macro parse( final String text, final int startPosition )
    {
        macroName = "";
        attributes.clear();
        body = "";

        input = text;
        if ( startPosition < 0 || startPosition > input.length() )
        {
            throw new ParseException( "Invalid start position: " + startPosition );
        }

        p = startPosition;
        c = input.charAt( p );

        try
        {
            doParse();
        }
        catch ( ParseException e )
        {
            if ( debugMode )
            {
                throw e;
            }
            return null;
        }

        final Macro.Builder macro = Macro.create().name( macroName );
        for ( String attribute : attributes.keySet() )
        {
            for ( String value : attributes.get( attribute ) )
            {
                macro.param( attribute, value );
            }
        }
        return macro.body( body ).build();
    }

    public int parsedEndPos()
    {
        return p;
    }

    private void doParse()
    {
        ws();

        match( '[' );
        parseMacroName();
        parseAttributes();
        parseEndTag();
    }

    private void parseMacroName()
    {
        ws();
        if ( lookAhead() != Token.NAME )
        {
            throw new ParseException( "Expected macro name" );
        }
        macroName = parseName();
    }

    private void parseAttributes()
    {
        while ( lookAhead() == Token.NAME )
        {
            match( ' ' );
            parseAttribute();
        }
    }

    private void parseAttribute()
    {
        ws();
        final String name = parseName();
        ws();
        match( '=' );
        ws();
        match( '"' );
        final String value = StringEscapeUtils.unescapeHtml( parseAttributeValue() );
        match( '"' );
        this.attributes.put( name, value );
    }

    private String parseName()
    {
        final StringBuilder name = new StringBuilder();
        while ( isNameChar( c ) )
        {
            name.append( c );
            consume();
        }
        return name.toString();
    }

    private String parseAttributeValue()
    {
        final StringBuilder value = new StringBuilder();
        while ( c != '"' && c != EOF )
        {
            if ( c == '\\' )
            {
                consume();
                if ( c == '\\' )
                {
                    value.append( '\\' ); // escape backslash \\
                    consume();
                }
                else if ( c == '"' )
                {
                    value.append( '"' ); // escape quote \"
                    consume();
                }
            }
            else
            {
                value.append( c );
                consume();
            }
        }
        return value.toString();
    }

    private void parseEndTag()
    {
        final Token next = lookAhead();
        ws();
        if ( next == Token.SLASH )
        {
            parseWithoutBody();
            return;
        }
        else if ( next == Token.RIGHT_BRACKET )
        {
            parseWithBody();
            return;
        }

        throw new ParseException( "Expected closing of macro tag at position " + p );
    }

    private void parseWithoutBody()
    {
        match( '/' );
        match( ']' );
    }

    private void parseWithBody()
    {
        match( ']' );
        parseBody();
        match( '[' );
        match( '/' );
        match( macroName );
        match( ']' );
    }

    private void parseBody()
    {
        final String closingTag = "[/" + macroName + "]";
        final StringBuilder bodyStr = new StringBuilder();
        while ( c != EOF )
        {
            if ( c == '[' )
            {
                final String lookAheadText = input.substring( p, Math.min( p + closingTag.length(), input.length() ) );
                if ( closingTag.equals( lookAheadText ) )
                {
                    break;
                }
            }
            bodyStr.append( c );
            consume();
        }
        body = bodyStr.toString();
    }

    private Token lookAhead()
    {
        final int p = this.p;
        final char c = this.c;
        final Token t = nextToken();
        this.p = p;
        this.c = c;
        return t;
    }

    private Token nextToken()
    {
        while ( c != EOF )
        {
            if ( Character.isWhitespace( c ) )
            {
                ws();
                continue;
            }
            switch ( c )
            {
                case '"':
                    consume();
                    return Token.QUOTE;
                case '[':
                    consume();
                    return Token.LEFT_BRACKET;
                case ']':
                    consume();
                    return Token.RIGHT_BRACKET;
                case '=':
                    consume();
                    return Token.EQUALS;
                case '/':
                    consume();
                    return Token.SLASH;
                default:
                    if ( isNameChar( c ) )
                    {
                        if ( c == '_' )
                        {
                            throw new ParseException( "Name cannot start with underscore '" + c + "' at position " + p );
                        }
                        if ( c == '-' )
                        {
                            throw new ParseException( "Name cannot start with a hyphen character '" + c + "' at position " + p );
                        }
                        return Token.NAME;
                    }
                    throw new ParseException( "Invalid character '" + c + "' at position " + p );
            }
        }
        return null;
    }

    private void consume()
    {
        p++;
        c = p >= input.length() ? EOF : input.charAt( p );
    }

    private void ws()
    {
        while ( Character.isWhitespace( c ) )
        {
            consume();
        }
    }

    private void match( final char x )
    {
        if ( c == x )
        {
            consume();
        }
        else
        {
            throw new ParseException( "Expected '" + x + "', found '" + c + "' at position " + p );
        }
    }

    private void match( final String text )
    {
        for ( char x : text.toCharArray() )
        {
            if ( c == x )
            {
                consume();
            }
            else
            {
                throw new ParseException( "Expected '" + text + "', found '" + c + "' at position " + p );
            }
        }
    }

    private boolean isNameChar( final char c )
    {
        return Character.isLetterOrDigit( c ) || c == '_' || c == '-';
    }

    MacroParser debugMode()
    {
        this.debugMode = true;
        return this;
    }

}