package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.region.TextComponent;

import static com.enonic.xp.portal.impl.rendering.RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE;

@Component(immediate = true, service = Renderer.class)
public final class TextRenderer
    implements Renderer<TextComponent>
{
    PortalUrlService service;

    private static final String COMPONENT_HTML = "<section " + PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\">{1}</section>";

    @Override
    public Class<TextComponent> getType()
    {
        return TextComponent.class;
    }

    @Override
    public PortalResponse render( final TextComponent textComponent, final PortalRequest portalRequest )
    {
        final RenderMode renderMode = portalRequest.getMode();
        final PortalResponse.Builder portalResponseBuilder = PortalResponse.create();

        portalResponseBuilder.contentType( MediaType.HTML_UTF_8 ).postProcess( false );

        final String text = textComponent.getText();
        if ( renderMode == RenderMode.EDIT )
        {
            portalResponseBuilder.body( MessageFormat.format( COMPONENT_HTML, textComponent.getType().toString(), text ) );
        }
        else
        {
            if ( text.isEmpty() )
            {
                portalResponseBuilder.body( "" );
            }
            else
            {
                ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( portalRequest ).
                    value( text ).
                    customHtmlProcessor( processor -> {
                        processor.processDefault();
                        processor.getDocument().select( "figcaption:empty" ).
                            forEach( HtmlElement::remove );
                        return processor.getDocument().getInnerHtml();
                    } );

                final String processedHtml = service.processHtml( params );
                portalResponseBuilder.body( MessageFormat.format( COMPONENT_HTML, textComponent.getType().toString(), processedHtml ) );
            }
        }

        return portalResponseBuilder.build();
    }

    @Reference
    public void setPortalUrlService( final PortalUrlService service )
    {
        this.service = service;
    }
}
