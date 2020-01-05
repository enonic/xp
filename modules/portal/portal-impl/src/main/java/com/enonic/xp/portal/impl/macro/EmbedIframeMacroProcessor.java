package com.enonic.xp.portal.impl.macro;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.util.HtmlHelper;

@Component(immediate = true, service = BuiltInMacroProcessor.class)
public class EmbedIframeMacroProcessor
    implements BuiltInMacroProcessor
{
    private static final Pattern IFRAME_PATTERN = Pattern.compile( "(<iframe.*</iframe>)|(&lt;iframe.*&lt;/iframe&gt;)" );

    @Override
    public String getName()
    {
        return "embed";
    }

    @Override
    public PortalResponse process( final MacroContext macroContext )
    {
        if ( !isIframeHtml( macroContext.getBody() ) )
        {
            return generateNonIframeResponse( macroContext );
        }

        return generateResponse( HtmlHelper.unescape( macroContext.getBody() ) );
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
        return body != null && IFRAME_PATTERN.matcher( body ).find();
    }

    private RenderMode getRenderingMode( final MacroContext macroContext )
    {
        return macroContext.getRequest() == null ? RenderMode.LIVE : macroContext.getRequest().getMode();
    }
}
