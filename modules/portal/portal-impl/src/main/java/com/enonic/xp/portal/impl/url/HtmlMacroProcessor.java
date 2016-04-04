package com.enonic.xp.portal.impl.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.macro.Macro;
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
    private static final String NO_COMMENT_CHAR = "(?:[^\\\\]|^)";

    private static final String CATCHING_MACRO_NAME = "(?<name>\\w+)";

    private static final String MACRO_TAG =
        NO_COMMENT_CHAR + "(?<tag>\\[" + OPTIONAL_SPACE + CATCHING_MACRO_NAME + ATTRIBUTES + OPTIONAL_SPACE + "(?<empty>/)?\\])";

    /* Patterns */
    private static final Pattern COMPILED_MACRO_TAG = Pattern.compile( MACRO_TAG );

    /* Variables */
    private MacroService macroService;

    public HtmlMacroProcessor( final MacroService macroService )
    {
        this.macroService = macroService;
    }

    public String process( final String text )
    {
        StringBuilder processedText = new StringBuilder();
        int index = 0;

        final Matcher matcher = COMPILED_MACRO_TAG.matcher( text );
        while ( matcher.find( index ) )
        {
            int matchStartIndex = matcher.start( "tag" );
            int matchEndIndex = matcher.end();

            //Appends previous text
            processedText.append( text.substring( index, matchStartIndex ) );

            //If the macro has a body
            String entireMacro = null;
            if ( matcher.group( "empty" ) == null )
            {
                //Searches for the ending macro tag
                final String macroName = matcher.group( "name" );
                final Pattern compiledEndingMacroTag = generateCompiledEndingMacroTag( macroName );
                final Matcher endingMacroTagMatcher = compiledEndingMacroTag.matcher( text.substring( matchEndIndex ) );

                //If there is an ending macro tag
                if ( endingMacroTagMatcher.find() )
                {
                    //The macro to parse if from the opening macro tag to the ending macro tag
                    entireMacro = text.substring( matchStartIndex, matchEndIndex + endingMacroTagMatcher.end() );
                    index = matchEndIndex + endingMacroTagMatcher.end();
                }
                else
                {
                    //Else there is an error. Stops the parsing of the text
                    break;
                }
            }
            else
            {
                //Else the macro to parse is the current match
                entireMacro = matcher.group( "tag" );
                index = matchEndIndex;
            }

            if ( entireMacro != null )
            {
                //Calls the macro service to parse the macro
                final Macro parsedMacro = macroService.parse( entireMacro );

                //If the macro is incorrect.
                if ( parsedMacro == null )
                {
                    //Appends the macro unparsed. Should never happened
                    processedText.append( text.substring( matchStartIndex, matchEndIndex ) );
                }
                else
                {
                    final String serializedMacro = macroService.postProcessInstructionSerialize( parsedMacro );
                    processedText.append( serializedMacro );
                }
            }

        }

        //Concatenates the rest of the text
        processedText.append( text.substring( index ) );

        return processedText.toString();
    }

    private Pattern generateCompiledEndingMacroTag( String macroName )
    {
        String endingMacroTagPattern = NO_COMMENT_CHAR + "\\[/" + OPTIONAL_SPACE + macroName + OPTIONAL_SPACE + "\\]";
        return Pattern.compile( endingMacroTagPattern );
    }
}
