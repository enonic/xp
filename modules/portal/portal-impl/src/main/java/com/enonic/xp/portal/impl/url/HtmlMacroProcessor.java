package com.enonic.xp.portal.impl.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.macro.MacroService;

public class HtmlMacroProcessor
{
    private static final int MATCH_INDEX = 1;

    private static final Pattern MACROS_PATTERN = Pattern.compile( "[^\\\\](\\[(\\w+)(\\s(\\w*=.[^\\[\\]]*))*\\s?(/|\\].*\\[/\\2)\\])" );

    private MacroService macroService;

    public HtmlMacroProcessor( final MacroService macroService )
    {
        this.macroService = macroService;
    }

    public String process( final String test )
    {
        String result = test;

        final Matcher contentMatcher = MACROS_PATTERN.matcher( test );

        while ( contentMatcher.find() )
        {
            final String match = contentMatcher.group( MATCH_INDEX );
            result = result.replace( match, macroService.postProcessInstructionSerialize( macroService.parse( match ) ) );
        }

        return result;
    }
}
