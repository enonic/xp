package com.enonic.wem.portal.internal.content.page;

import java.text.MessageFormat;

import com.enonic.wem.api.content.page.text.TextComponent;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.internal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;

public final class TextRenderer
    implements Renderer<TextComponent>
{
    private static final String EMPTY_COMPONENT_EDIT_MODE_HTML =
        "<div data-live-edit-type=\"{0}\" data-live-edit-empty-component=\"true\" class=\"live-edit-empty-component\"></div>";

    private static final String EMPTY_COMPONENT_PREVIEW_MODE_HTML = "<div></div>";

    @Override
    public Class<TextComponent> getType()
    {
        return TextComponent.class;
    }

    @Override
    public RenderResult render( final TextComponent textComponent, final PortalContext context )
    {
        final RenderingMode renderingMode = getRenderingMode( context );
        final PortalResponse response = context.getResponse();
        response.setContentType( "text/html" );
        response.setPostProcess( false );

        if ( textComponent.getText() == null )
        {
            renderEmptyTextComponent( textComponent, context );
        }
        else
        {
            if ( renderingMode == RenderingMode.EDIT )
            {
                response.setBody( MessageFormat.format( "<div data-live-edit-type=\"{0}\">{1}</div>", textComponent.getType().toString(),
                                                        textComponent.getText() ) );
            }
            else
            {
                response.setBody( textComponent.getText() );
            }
        }

        return new JsHttpResponseSerializer( response ).serialize();
    }

    private void renderEmptyTextComponent( final TextComponent textComponent, final PortalContext context )
    {
        final PortalResponse response = context.getResponse();
        final RenderingMode renderingMode = getRenderingMode( context );
        switch ( renderingMode )
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

    private RenderingMode getRenderingMode( final PortalContext context )
    {
        final PortalRequest req = context.getRequest();
        return req == null ? RenderingMode.LIVE : req.getMode();
    }
}
