package com.enonic.xp.impl.macro;

import java.util.function.Function;

import com.enonic.xp.macro.Macro;

/**
 * Find and evaluate macros, in HTML tags body only (ignore attributes or HTML comments).
 * Based on HTML5 tokenization algorithm: https://www.w3.org/TR/2011/WD-html5-20110113/tokenization.html
 */
final class HtmlMacroEvaluator
{
    private static final char EOF = (char) -1;

    private String input;

    private final Function<Macro, String> macroProcessor;

    private int p;

    private char c;

    HtmlMacroEvaluator( final String input, final Function<Macro, String> macroProcessor )
    {
        this.input = input;
        this.macroProcessor = macroProcessor;
    }

    String evaluate()
    {
        p = -1;
        process();

        return input;
    }

    private void process()
    {
        do
        {
            nextChar();
            switch ( c )
            {
                case '<':
                    processTagOpen();
                    break;

                case '[':
                    processMacro();
                    break;
            }
        }
        while ( c != EOF );
    }

    private void processMacro()
    {
        final MacroParser parser = new MacroParser();

        if ( p > 1 && input.charAt( p - 1 ) == '\\' )
        {
            return; // ignore escaped opening bracket
        }

        final Macro macro = parser.parse( input, p );
        if ( macro != null )
        {
            final String replacement = macroProcessor.apply( macro );
            input = input.substring( 0, p ) + replacement + input.substring( parser.parsedEndPos() );
            p = p + replacement.length() - 1;
        }
    }

    private void processTagOpen()
    {
        nextChar();
        if ( c == '!' )
        {
            processMarkupDeclarationOpen();
        }
        else if ( c == '/' )
        {
            processEndTagOpen();
        }
        else if ( Character.isLetter( c ) )
        {
            processTagName();
        }
    }

    private void processMarkupDeclarationOpen()
    {
        nextChar();
        if ( c == '-' && lookAhead() == '-' )
        {
            nextChar();
            processCommentStart();
        }
        else if ( "DOCTYPE".equalsIgnoreCase( lookAheadStr( 7 ) ) )
        {
            p += 7;
            processDoctype();
        }
        else if ( "[CDATA[".equalsIgnoreCase( lookAheadStr( 7 ) ) )
        {
            p += 7;
            processCdataSection();
        }
        else
        {
            processBogusComment();
        }
    }

    private void processCdataSection()
    {
        do
        {
            if ( "]]>".equals( lookAheadStr( 3 ) ) )
            {
                p += 2;
                return;
            }
            nextChar();
        }
        while ( c != EOF );
    }

    private void processDoctype()
    {
        do
        {
            nextChar();
            if ( c == '>' )
            {
                return;
            }
        }
        while ( c != EOF );
    }

    private void processCommentStart()
    {
        nextChar();
        if ( c == '-' )
        {
            processCommentStartDash();
        }
        else
        {
            processComment();
        }
    }

    private void processCommentStartDash()
    {
        nextChar();
        if ( c == '-' )
        {
            processCommentEnd();
        }
        else
        {
            processComment();
        }
    }

    private void processComment()
    {
        do
        {
            nextChar();
            if ( c == '-' )
            {
                processCommentEndDash();
                return;
            }
        }
        while ( c != EOF );
    }

    private void processCommentEndDash()
    {
        nextChar();
        if ( c == '-' )
        {
            processCommentEnd();
        }
        else
        {
            processComment();
        }
    }

    private void processCommentEnd()
    {
        nextChar();
        if ( c == '>' )
        {
            return;
        }
        else
        {
            processComment();
        }
    }

    private void processTagName()
    {
        do
        {
            nextChar();
            if ( Character.isSpaceChar( c ) )
            {
                processBeforeAttributeName();
                return;
            }
            else if ( c == '/' )
            {
                processSelfClosingStartTag();
                return;
            }
            else if ( c == '>' )
            {
                return;
            }
        }
        while ( c != EOF );
    }

    private void processBeforeAttributeName()
    {
        do
        {
            nextChar();
            if ( Character.isSpaceChar( c ) )
            {
                continue;
            }
            else if ( c == '/' )
            {
                processSelfClosingStartTag();
                return;
            }
            else if ( c == '>' )
            {
                return;
            }
            else if ( Character.isLetter( c ) || c == '"' || c == '\'' || c == '<' || c == '=' )
            {
                processAttributeName();
                return;
            }
        }
        while ( c != EOF );
    }

    private void processAttributeName()
    {
        do
        {
            nextChar();
            if ( Character.isSpaceChar( c ) )
            {
                processAfterAttributeName();
                return;
            }
            else if ( c == '/' )
            {
                processSelfClosingStartTag();
                return;
            }
            else if ( c == '=' )
            {
                processBeforeAttributeValue();
                return;
            }
            else if ( c == '>' )
            {
                return;
            }
        }
        while ( c != EOF );
    }

    private void processAfterAttributeName()
    {
        do
        {
            nextChar();
            if ( Character.isSpaceChar( c ) )
            {
                continue;
            }
            else if ( c == '/' )
            {
                processSelfClosingStartTag();
                return;
            }
            else if ( c == '=' )
            {
                processBeforeAttributeValue();
                return;
            }
            else if ( c == '>' )
            {
                return;
            }
            else if ( Character.isLetter( c ) || c == '"' || c == '\'' || c == '<' )
            {
                processAttributeName();
                return;
            }
        }
        while ( c != EOF );
    }

    private void processBeforeAttributeValue()
    {
        do
        {
            nextChar();
            if ( Character.isSpaceChar( c ) )
            {
                continue;
            }
            else if ( c == '"' )
            {
                processAttributeValueDoubleQuoted();
                return;
            }
            else if ( c == '\'' )
            {
                processAttributeValueSingleQuoted();
                return;
            }
            else
            {
                processAttributeValueUnquoted();
                return;
            }
        }
        while ( c != EOF );
    }

    private void processAttributeValueUnquoted()
    {
        do
        {
            nextChar();
            if ( Character.isSpaceChar( c ) )
            {
                processBeforeAttributeName();
                return;
            }
            else if ( c == '>' )
            {
                return;
            }
        }
        while ( c != EOF );
    }

    private void processAttributeValueDoubleQuoted()
    {
        do
        {
            nextChar();
            if ( c == '"' )
            {
                processAfterAttributeValueQuoted();
                return;
            }
        }
        while ( c != EOF );
    }

    private void processAttributeValueSingleQuoted()
    {
        do
        {
            nextChar();
            if ( c == '\'' )
            {
                processAfterAttributeValueQuoted();
                return;
            }
        }
        while ( c != EOF );
    }

    private void processAfterAttributeValueQuoted()
    {
        do
        {
            nextChar();
            if ( Character.isSpaceChar( c ) )
            {
                processBeforeAttributeName();
                return;
            }
            else if ( c == '/' )
            {
                processSelfClosingStartTag();
                return;
            }
            else if ( c == '>' )
            {
                return;
            }
        }
        while ( c != EOF );
    }

    private void processSelfClosingStartTag()
    {
        nextChar();
        if ( c == '>' )
        {
            return;
        }
        else
        {
            p--;
            processBeforeAttributeName();
        }
    }

    private void processEndTagOpen()
    {
        nextChar();
        if ( c == '>' )
        {
            return;
        }
        else if ( Character.isLetter( c ) )
        {
            processTagName();
        }
        else
        {
            processBogusComment();
        }
    }

    private void processBogusComment()
    {
        do
        {
            nextChar();
            if ( c == '>' )
            {
                return;
            }
        }
        while ( c != EOF );
    }

    private void nextChar()
    {
        p++;
        c = p >= input.length() ? EOF : input.charAt( p );
    }

    private char lookAhead()
    {
        int nextP = p + 1;
        return nextP >= input.length() ? EOF : input.charAt( nextP );
    }

    private String lookAheadStr( int length )
    {
        int nextP = p + length;
        return nextP >= input.length() ? "" : input.substring( p, p + length );
    }
}
