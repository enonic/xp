package com.enonic.xp.portal.impl.macro;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.internal.HtmlHelper;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.macro.MacroContext;

@Component(immediate = true, service = BuiltInMacroProcessor.class)
public class EmbedIframeMacroProcessor
    implements BuiltInMacroProcessor
{
    private static final Pattern IFRAME_PATTERN = Pattern.compile( "^<iframe.*</iframe>$" );

    @Override
    public String getName()
    {
        return "embed";
    }

    @Override
    public PortalResponse process( final MacroContext macroContext )
    {
        final String body = HtmlHelper.unescape( macroContext.getBody() );
        if ( isIframeHtml( body ) )
        {
            return generateResponse( body );
        }
        return generateNonIframeResponse( macroContext );

    }

    private PortalResponse generateNonIframeResponse( final MacroContext macroContext )
    {
        if ( RenderMode.LIVE.equals( getRenderingMode( macroContext ) ) )
        {
            return generateResponse( "" );
        }

        return generateResponse( "Expected an &lt;iframe&gt; element in Embed macro" );
    }

    private PortalResponse generateResponse( final String body )
    {
        return PortalResponse.create().body( body ).build();
    }

    private boolean isIframeHtml( final String body )
    {
        return IFRAME_PATTERN.matcher( body ).matches();
    }

    private RenderMode getRenderingMode( final MacroContext macroContext )
    {
        return macroContext.getRequest() == null ? RenderMode.LIVE : macroContext.getRequest().getMode();
    }
}
