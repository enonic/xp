package com.enonic.xp.portal.impl.macro;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;

public class YoutubeMacroProcessor
    implements MacroProcessor
{

    private static final String URL_PARAM = "url";

    private static final String WIDTH_PARAM = "width";

    private static final String HEIGHT_PARAM = "height";

    @Override
    public PortalResponse process( final MacroContext context )
    {
        if ( !hasAttr( context, URL_PARAM ) )
        {
            return null;
        }

        final String html =
            "<iframe src=\"" + context.getParam( URL_PARAM ) + "\"" + makeWidthAttr( context ) + makeHeightAttr( context ) + "></iframe>";

        return PortalResponse.create().body( html ).build();
    }

    private boolean hasAttr( final MacroContext macroContext, final String name )
    {
        return macroContext.getParam( name ) != null;
    }

    private String makeWidthAttr( final MacroContext macroContext )
    {
        final String result = macroContext.getParam( WIDTH_PARAM );
        return result != null ? " width=\"" + result + "\"" : "";
    }

    private String makeHeightAttr( final MacroContext macroContext )
    {
        final String result = macroContext.getParam( HEIGHT_PARAM );
        return result != null ? " height=\"" + result + "\"" : "";
    }
}
