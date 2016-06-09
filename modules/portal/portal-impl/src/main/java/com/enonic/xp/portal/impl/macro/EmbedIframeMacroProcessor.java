package com.enonic.xp.portal.impl.macro;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.macro.MacroContext;

@Component(immediate = true, service = BuiltInMacroProcessor.class)
public class EmbedIframeMacroProcessor
    implements BuiltInMacroProcessor
{
    private static final Pattern IFRAME_PATTERN = Pattern.compile( "^(<iframe|&lt;iframe).*(</iframe>|&lt;/iframe&gt;)$", Pattern.DOTALL );

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

        return generateResponse( encodeIframeBodyInTheSrcAttr( StringEscapeUtils.unescapeHtml( macroContext.getBody() ) ) );
    }

    private String encodeIframeBodyInTheSrcAttr( final String body )
    {

        return appendBodyAsSrcToOpeningTag( getOpeningTag( body ), getIframeBody( body ) ) + "</iframe>";
    }

    private String appendBodyAsSrcToOpeningTag( final String openingTag, final String body )
    {
        return openingTag.substring( 0, openingTag.length() - 1 ) + " src=\"data:text/html;charset=utf-8," +
            body.replaceAll( "\"", "&quot;" ) + "\">";
    }

    private String getOpeningTag( final String iframeString )
    {
        return iframeString.substring( 0, iframeString.indexOf( ">" ) + 1 );
    }

    private String getIframeBody( final String iframeString )
    {
        return iframeString.substring( iframeString.indexOf( ">" ) + 1, iframeString.lastIndexOf( "</" ) );
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
