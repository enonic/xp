package com.enonic.xp.portal.impl.macro;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;

public class TwitterMacroProcessor
    implements MacroProcessor
{

    private static final String LANG_WILDCARD = "{LANG}";

    private static final String DEFAULT_LANG = "en";

    private static final String URL_WILDCARD = "{URL}";

    private static final String TWITTER_OUTPUT =
        "<blockquote class=\"twitter-tweet\" lang=\"{LANG}\"><a hidden=\"true\" href=\"{URL}\">Link to tweet</a></blockquote>\n" +
            "<script src=\"//platform.twitter.com/widgets.js\" async=\"\" charset=\"utf-8\">";

    @Override
    public PortalResponse process( final MacroContext macroContext )
    {
        if ( !hasUrlAttr( macroContext ) )
        {
            return null;
        }
        final String html =
            TWITTER_OUTPUT.replace( LANG_WILDCARD, getLanguageAttr( macroContext ) ).replace( URL_WILDCARD, getUrlAttr( macroContext ) );
        return PortalResponse.create().body( html ).build();
    }

    private boolean hasUrlAttr( final MacroContext macroContext )
    {
        return macroContext.getParam( "url" ) != null;
    }

    private String getUrlAttr( final MacroContext macroContext )
    {
        return macroContext.getParam( "url" );
    }

    private String getLanguageAttr( final MacroContext macroContext )
    {
        final String langAttr = macroContext.getParam( "lang" );
        return langAttr != null ? langAttr : DEFAULT_LANG;
    }
}
