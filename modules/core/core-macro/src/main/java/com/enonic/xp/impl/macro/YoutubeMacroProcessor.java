package com.enonic.xp.impl.macro;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

public class YoutubeMacroProcessor
    implements MacroProcessor
{

    private static final String URL_PARAM = "url";

    private static final String WIDTH_PARAM = "width";

    private static final String HEIGHT_PARAM = "height";

    private static final char QUOTE = '\"';

    @Override
    public String process( final MacroContext context )
    {
        if ( !hasAttr( context, URL_PARAM ) )
        {
            return null;
        }

        return new StringBuilder().append( "<iframe src=\"" ).append( context.getParam( URL_PARAM ) ).append( QUOTE ).append(
            makeWidthAttr( context ) ).append( makeHeightAttr( context ) ).append( "></iframe>" ).toString();
    }

    private boolean hasAttr( final MacroContext macroContext, final String name )
    {
        return macroContext.getParam( name ) != null;
    }

    private String makeWidthAttr( final MacroContext macroContext )
    {
        final String result = macroContext.getParam( WIDTH_PARAM );
        return result != null ? " width=\"" + result + QUOTE : "";
    }

    private String makeHeightAttr( final MacroContext macroContext )
    {
        final String result = macroContext.getParam( HEIGHT_PARAM );
        return result != null ? " height=\"" + result + QUOTE : "";
    }
}
