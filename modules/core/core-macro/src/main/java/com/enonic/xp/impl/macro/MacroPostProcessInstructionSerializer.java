package com.enonic.xp.impl.macro;

import com.google.common.collect.ImmutableMultimap;

import com.enonic.xp.macro.Macro;

public class MacroPostProcessInstructionSerializer
{

    private static final String START_TAG = "<!--#MACRO ";

    private static final String END_TAG = "-->";

    public String serialize( final Macro macro )
    {
        return START_TAG + makeNameAttribute( macro ) + makeParamsAttributes( macro ) + makeBodyAttribute( macro ) + END_TAG;
    }

    private String makeNameAttribute( final Macro macro )
    {
        return "_name=\"" + macro.getName() + "\"";
    }

    private String makeParamsAttributes( final Macro macro )
    {
        final StringBuilder result = new StringBuilder( "" );
        final ImmutableMultimap<String, String> params = macro.getParameters();
        for ( String key : params.keySet() )
        {
            for ( String value : params.get( key ) )
            {
                String escapedVal = escapeSpecialChars( value );
                result.append( " " ).append( key ).append( "=\"" ).append( escapedVal ).append( "\"" );
            }
        }
        return result.toString();
    }

    private String makeBodyAttribute( final Macro macro )
    {
        String body = "";
        if ( macro.getBody() != null )
        {
            body = escapeSpecialChars( macro.getBody() );
        }
        return " _body=\"" + body + "\"";
    }

    private String escapeSpecialChars( final String str )
    {
        return str.replaceAll( "\"", "\\\\\"" ).replaceAll( "--", "\\\\-\\\\-" );
    }

}
