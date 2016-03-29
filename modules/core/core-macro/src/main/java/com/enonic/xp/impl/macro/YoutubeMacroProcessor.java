package com.enonic.xp.impl.macro;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

public class YoutubeMacroProcessor
    implements MacroProcessor
{

    private static final String URL_WILDCARD = "{URL}";

    private static final String WIDTH_WILDCARD = "{WIDTH}";

    private static final String HEIGHT_WILDCARD = "{HEIGHT}";

    private static final String YOUTUBE_OUTPUT = "<iframe width=\"{WIDTH}\" height=\"{HEIGHT}\" src=\"{URL}\"></iframe>";

    @Override
    public String process( final MacroContext context )
    {
        if ( !hasUrlAttr( context ) )
        {
            return null;
        }
        return YOUTUBE_OUTPUT.
            replace( WIDTH_WILDCARD, getWidthAttr( context ) ).
            replace( HEIGHT_WILDCARD, getHeightAttr( context ) ).
            replace( URL_WILDCARD, getUrlAttr( context ) );
    }

    private boolean hasUrlAttr( final MacroContext macroContext )
    {
        return macroContext.getParam( "url" ) != null;
    }

    private String getUrlAttr( final MacroContext macroContext )
    {
        return macroContext.getParam( "url" );
    }

    private String getWidthAttr( final MacroContext macroContext )
    {
        final String result = macroContext.getParam( "width" );
        return result != null ? result : "";
    }

    private String getHeightAttr( final MacroContext macroContext )
    {
        final String result = macroContext.getParam( "height" );
        return result != null ? result : "";
    }
}
