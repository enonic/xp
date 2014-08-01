package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.content.page.text.TextComponent;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.rendering.RenderResult;
import com.enonic.wem.portal.rendering.Renderer;


public final class TextRenderer
    implements Renderer<TextComponent>
{
    @Override
    public RenderResult render( final TextComponent component, final JsContext context )
    {
        context.getResponse().setBody( component.getText() );
        return new JsHttpResponseSerializer( context.getResponse() ).serialize();
    }
}
