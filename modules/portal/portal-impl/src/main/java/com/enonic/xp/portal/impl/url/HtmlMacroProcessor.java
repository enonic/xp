package com.enonic.xp.portal.impl.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.macro.MacroService;

public class HtmlMacroProcessor
{
    /* Generics Regexps */
    private static final String MANDATORY_SPACE = "\\s+";

    private static final String OPTIONAL_SPACE = "\\s*";

    /* Attributes Regexps */
    private static final String ATTRIBUTE_KEY = "\\w+";

    private static final String ATTRIBUTE_VALUE = "\"[^\"]+\"";

    private static final String ATTRIBUTE_ENTRY = ATTRIBUTE_KEY + OPTIONAL_SPACE + "=" + OPTIONAL_SPACE + ATTRIBUTE_VALUE;

    private static final String ATTRIBUTES = "(?:" + MANDATORY_SPACE + ATTRIBUTE_ENTRY + ")*";

    /* Macro tags Regexps */
    private static final String NO_COMMENT_CHAR = "[^\\\\]";

    private static final String CATCHING_MACRO_NAME = "(?<name>\\w+)";

    private static final String EMPTY_MACRO =
        NO_COMMENT_CHAR + "\\[" + OPTIONAL_SPACE + CATCHING_MACRO_NAME + ATTRIBUTES + OPTIONAL_SPACE + "/\\]";

    private static final String OPENING_MACRO_TAG =
        NO_COMMENT_CHAR + "\\[" + OPTIONAL_SPACE + CATCHING_MACRO_NAME + ATTRIBUTES + OPTIONAL_SPACE + "\\]";

    private static final String ENDING_MACRO_TAG = NO_COMMENT_CHAR + "\\[/" + OPTIONAL_SPACE + CATCHING_MACRO_NAME + OPTIONAL_SPACE + "\\]";

    /* Patterns */
    private static final Pattern COMPILED_EMPTY_MACRO = Pattern.compile( EMPTY_MACRO );

    private static final Pattern COMPILED_OPENING_MACRO_TAG = Pattern.compile( OPENING_MACRO_TAG );

    private static final Pattern COMPILED_ENDING_MACRO_TAG = Pattern.compile( ENDING_MACRO_TAG );

    /* Variables */
    private MacroService macroService;

    public HtmlMacroProcessor( final MacroService macroService )
    {
        this.macroService = macroService;
    }

    public String process( final String text )
    {
        final Matcher matcher = COMPILED_EMPTY_MACRO.matcher( text );
        System.out.println( matcher.find() );
        System.out.println( matcher.group( "name" ) );
        System.out.println( matcher.find() );
        System.out.println( matcher.group( "name" ) );

        return null;
    }

    public static void main( String[] args )
    {
        new HtmlMacroProcessor( null ).process(
            "<a href=\"[macroNoBody /]\">[macro par1=\"val1\" par2=\"val2\"/]</a> \\[macroName]skip me[/macroName]" );
    }
}
