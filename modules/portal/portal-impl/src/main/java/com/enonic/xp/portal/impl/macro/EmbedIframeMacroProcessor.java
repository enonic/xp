package com.enonic.xp.portal.impl.macro;

import org.osgi.service.component.annotations.Component;

import com.google.common.net.MediaType;

import com.enonic.xp.core.internal.HtmlHelper;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.macro.MacroContext;

@Component(immediate = true, service = BuiltInMacroProcessor.class)
public class EmbedIframeMacroProcessor
    implements BuiltInMacroProcessor
{
    @Override
    public String getName()
    {
        return "embed";
    }

    @Override
    public PortalResponse process( final MacroContext macroContext )
    {
        final String macroBody = macroContext.getBody();
        final String body;
        if ( isIframeHtml( macroBody ) )
        {
            body = HtmlHelper.unescape( macroBody );
        }
        else
        {
            body = macroContext.getRequest().getMode() == RenderMode.LIVE ? "" : "Expected an &lt;iframe&gt; element in Embed macro";
        }
        return PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).body( body ).build();
    }

    private static boolean isIframeHtml( final String body )
    {
        if ( body == null )
        {
            return false;
        }
        // embed tag should be always escaped
        return body.startsWith( "&lt;iframe" ) && body.endsWith( "&lt;/iframe&gt;" );
    }
}
