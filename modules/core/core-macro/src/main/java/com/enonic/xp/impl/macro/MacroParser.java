package com.enonic.xp.impl.macro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroKey;

public class MacroParser
{

    private final static Pattern VALID_MACRO_PATTERN = Pattern.compile( "^\\[(\\w+)(\\s(\\w*=.[^\\[\\]]*))*\\s?(/|\\].*\\[/\\1)\\]$" );

    private final static Pattern MACRO_WITHOUT_BODY_PATTERN = Pattern.compile( "^\\[(\\w+)((\\s\\w*=.[^\\[\\]]*)*)\\s?/\\]$" );

    private final static Pattern MACRO_WITH_BODY_PATTERN = Pattern.compile( "^\\[(\\w+)((\\s\\w*=.[^\\[\\]]*)*)\\s?\\](.*)\\[/\\1\\]$" );

    private final ApplicationKey applicationKey;

    public MacroParser( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
    }

    public Macro parse( final String text )
    {

        if ( !isValidMacro( text ) )
        {
            return null;
        }

        if ( isMacroWithoutBody( text ) )
        {
            return parseMacroWithoutBody( text );
        }

        return parseMacroWithBody( text );
    }

    private Macro parseMacroWithBody( final String text )
    {
        final Matcher matcher = MACRO_WITH_BODY_PATTERN.matcher( text );

        if ( !matcher.find() )
        {
            return null;
        }

        final String body = matcher.group( 4 );

        return populateCommonData( matcher ).body( body ).build();
    }

    private Macro parseMacroWithoutBody( final String text )
    {
        final Matcher matcher = MACRO_WITHOUT_BODY_PATTERN.matcher( text );

        if ( !matcher.find() )
        {
            return null;
        }

        return populateCommonData( matcher ).build();
    }

    private Macro.Builder populateCommonData( final Matcher matcher )
    {
        final String macroName = matcher.group( 1 );
        final String attributesString = matcher.group( 2 );
        final Iterable<String> attributesList =
            Splitter.on( CharMatcher.WHITESPACE ).trimResults().omitEmptyStrings().split( attributesString );

        final Macro.Builder builder = Macro.create();
        builder.key( MacroKey.from( this.applicationKey, macroName ) );

        return populateAttributes( builder, attributesList );
    }

    private Macro.Builder populateAttributes( final Macro.Builder builder, final Iterable<String> attributesList )
    {
        for ( final String s : attributesList )
        {
            final String[] attrs = s.split( "=" );
            if ( attrs.length == 2 )
            {
                builder.param( attrs[0], attrs[1] );
            }
        }

        return builder;
    }

    public static boolean isValidMacro( final String text )
    {
        return VALID_MACRO_PATTERN.matcher( text ).matches();
    }

    private boolean isMacroWithoutBody( final String text )
    {
        return MACRO_WITHOUT_BODY_PATTERN.matcher( text ).matches();
    }
}
