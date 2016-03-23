package com.enonic.xp.impl.macro;

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
        return "_name=\"" + macro.getKey().getName() + "\"";
    }

    private String makeParamsAttributes( final Macro macro )
    {
        final StringBuilder result = new StringBuilder( "" );
        macro.getParams().forEach( ( key, value ) -> {
            String escapedVal = value.replaceAll( "\"", "\\\"" ).replaceAll( "--", "\\-\\-" );
            result.append( " " ).append( key ).append( "=\"" ).append( escapedVal ).append( "\"" );
        } );
        return result.toString();
    }

    private String makeBodyAttribute( final Macro macro )
    {
        return " _body=\"" + ( macro.getBody() != null ? macro.getBody() : "" ) + "\"";
    }

}
