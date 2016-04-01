package com.enonic.xp.impl.macro;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

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
    public String process( final MacroContext macroContext )
    {
        if ( !hasUrlAttr( macroContext ) )
        {
            return null;
        }
        return TWITTER_OUTPUT.replace( LANG_WILDCARD, getLanguageAttr( macroContext ) ).replace( URL_WILDCARD, getUrlAttr( macroContext ) );
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
