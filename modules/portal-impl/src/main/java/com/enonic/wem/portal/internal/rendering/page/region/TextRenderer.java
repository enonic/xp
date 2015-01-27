package com.enonic.wem.portal.internal.rendering.page.region;

import java.text.MessageFormat;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.content.page.region.TextComponent;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;

@Component(immediate = true, service = Renderer.class)
public final class TextRenderer
    implements Renderer<TextComponent>
{

    private static final String EMPTY_COMPONENT_EDIT_MODE_HTML = "<div data-live-edit-type=\"{0}\"><article></article></div>";

    private static final String EMPTY_COMPONENT_PREVIEW_MODE_HTML = "<article></article>";

    private static final String COMPONENT_EDIT_MODE_HTML = "<div data-live-edit-type=\"{0}\"><article>{1}</article></div>";

    private static final String COMPONENT_PREVIEW_MODE_HTML = "<article>{0}</article>";

    @Override
    public Class<TextComponent> getType()
    {
        return TextComponent.class;
    }

    @Override
    public RenderResult render( final TextComponent textComponent, final PortalContext context )
    {
        final RenderMode renderMode = getRenderingMode( context );
        final PortalResponse response = context.getResponse();
        response.setContentType( "text/html" );
        response.setPostProcess( false );

        if ( textComponent.getText() == null )
        {
            renderEmptyTextComponent( textComponent, context );
        }
        else
        {
            switch ( renderMode )
            {
                case EDIT:
                    response.setBody(
                        MessageFormat.format( COMPONENT_EDIT_MODE_HTML, textComponent.getType().toString(), textComponent.getText() ) );
                    break;

                case LIVE:
                case PREVIEW:
                default:
                    response.setBody( MessageFormat.format( COMPONENT_PREVIEW_MODE_HTML, textComponent.getText() ) );
                    break;
            }
        }

        return new PortalResponseSerializer( response ).serialize();
    }

    private void renderEmptyTextComponent( final TextComponent textComponent, final PortalContext context )
    {
        final PortalResponse response = context.getResponse();
        final RenderMode renderMode = getRenderingMode( context );
        switch ( renderMode )
        {
            case EDIT:
                response.setBody( MessageFormat.format( EMPTY_COMPONENT_EDIT_MODE_HTML, textComponent.getType().toString() ) );
                break;

            case PREVIEW:
                response.setBody( EMPTY_COMPONENT_PREVIEW_MODE_HTML );
                break;

            case LIVE:
                response.setBody( "" );
                break;
        }
    }

    private RenderMode getRenderingMode( final PortalContext context )
    {
        return context == null ? RenderMode.LIVE : context.getMode();
    }
}
