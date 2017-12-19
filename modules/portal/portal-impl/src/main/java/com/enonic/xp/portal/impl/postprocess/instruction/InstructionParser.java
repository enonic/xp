package com.enonic.xp.portal.impl.postprocess.instruction;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.portal.impl.rendering.RenderException;

final class InstructionParser
{
    private enum Token
    {
        NAME, QUOTE, EQUALS
    }

    private static final char EOF = (char) -1;

    private String input;

    private int p;

    private char c;

    private String instructionId;

    private ListMultimap<String, String> attributes;

    public Instruction parse( final String text )
    {
        instructionId = "";
        attributes = ArrayListMultimap.create();

        input = text;
        p = 0;
        c = input.charAt( p );

        doParse();

        return new Instruction( instructionId, attributes );
    }

    private void doParse()
    {
        parseInstructionId();
        parseAttributes();
    }

    private void parseInstructionId()
    {
        if ( lookAhead() != Token.NAME )
        {
            throw new RenderException( "Post-processing instruction: Expected instruction id" );
        }
        instructionId = parseName();
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
        final String value = parseAttributeValue();
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
                case '=':
                    consume();
                    return Token.EQUALS;
                default:
                    if ( isNameChar( c ) )
                    {
                        return Token.NAME;
                    }
                    throw new RenderException( "Post-processing instruction: Invalid character '" + c + "' at position " + p );
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
            throw new RenderException( "Post-processing instruction: Expected '" + x + "', found '" + c + "' at position " + p );
        }
    }

    private boolean isNameChar( final char c )
    {
        return Character.isLetterOrDigit( c ) || c == '_';
    }

}