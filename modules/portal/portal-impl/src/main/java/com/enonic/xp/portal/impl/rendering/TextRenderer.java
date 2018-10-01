package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.region.TextComponent;

import static com.enonic.xp.portal.impl.rendering.RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE;

@Component(immediate = true, service = Renderer.class)
public final class TextRenderer
    implements Renderer<TextComponent>
{

    PortalUrlService service;

    private static final String EMPTY_COMPONENT_EDIT_MODE_HTML =
        "<div " + PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"><section></section></div>";

    private static final String EMPTY_COMPONENT_PREVIEW_MODE_HTML = "<section " + PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></section>";

    private static final String COMPONENT_EDIT_MODE_HTML = "<div " + PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"><section>{1}</section></div>";

    private static final String COMPONENT_PREVIEW_MODE_HTML = "<section " + PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\">{1}</section>";

    private static final Pattern EMPTY_FIGCAPTION_PATTERN = Pattern.compile( "<figcaption.*?></figcaption>" );

    @Override
    public Class<TextComponent> getType()
    {
        return TextComponent.class;
    }

    @Override
    public PortalResponse render( final TextComponent textComponent, final PortalRequest portalRequest )
    {
        final RenderMode renderMode = getRenderingMode( portalRequest );
        final PortalResponse.Builder portalResponseBuilder = PortalResponse.create();

        portalResponseBuilder.contentType( MediaType.create( "text", "html" ) ).postProcess( false );

        if ( textComponent.getText().isEmpty() )
        {
            renderEmptyTextComponent( textComponent, portalRequest, portalResponseBuilder );
        }
        else
        {
            switch ( renderMode )
            {
                case EDIT:
                    portalResponseBuilder.body(
                        MessageFormat.format( COMPONENT_EDIT_MODE_HTML, textComponent.getType().toString(), textComponent.getText() ) );
                    break;

                case LIVE:
                case PREVIEW:
                case INLINE:
                default:
                    ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( portalRequest ).value( textComponent.getText() );
                    final String processedHtml = removeEmptyFigCaptionTags( service.processHtml( params ) );
                    portalResponseBuilder.body(
                        MessageFormat.format( COMPONENT_PREVIEW_MODE_HTML, textComponent.getType().toString(), processedHtml ) );
                    break;
            }
        }

        return portalResponseBuilder.build();
    }

    private void renderEmptyTextComponent( final TextComponent textComponent, final PortalRequest portalRequest,
                                           final PortalResponse.Builder portalResponseBuilder )
    {
        final RenderMode renderMode = getRenderingMode( portalRequest );
        switch ( renderMode )
        {
            case EDIT:
                portalResponseBuilder.body( MessageFormat.format( EMPTY_COMPONENT_EDIT_MODE_HTML, textComponent.getType().toString() ) );
                break;

            case PREVIEW:
            case INLINE:
                portalResponseBuilder.body( MessageFormat.format( EMPTY_COMPONENT_PREVIEW_MODE_HTML, textComponent.getType().toString() ) );
                break;

            case LIVE:
                portalResponseBuilder.body( "" );
                break;
        }
    }

    private RenderMode getRenderingMode( final PortalRequest portalRequest )
    {
        return portalRequest == null ? RenderMode.LIVE : portalRequest.getMode();
    }

    private String removeEmptyFigCaptionTags( final String text )
    {
        final Matcher matcher = EMPTY_FIGCAPTION_PATTERN.matcher( text );

        return matcher.replaceAll( "" );
    }

    @Reference
    public void setPortalUrlService( final PortalUrlService service )
    {
        this.service = service;
    }
}
