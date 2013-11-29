package com.enonic.wem.core.content.page.layout;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.RenderablesRegister;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RendererFactory;

@SuppressWarnings("UnusedDeclaration")
public class LayoutRendererFactory
    implements RendererFactory
{
    public static void register()
    {
        RenderablesRegister.get().register( LayoutComponent.class, new LayoutRendererFactory() );
    }

    private LayoutRendererFactory()
    {
        // prevention
    }

    @Override
    public Renderer create( final Client client, final Context context )
    {
        return new LayoutRenderer( client, context );
    }
}
