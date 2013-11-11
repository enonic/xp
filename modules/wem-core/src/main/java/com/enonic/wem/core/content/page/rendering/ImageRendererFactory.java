package com.enonic.wem.core.content.page.rendering;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Image;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.RenderablesRegister;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RendererFactory;

@SuppressWarnings("UnusedDeclaration")
public class ImageRendererFactory
    implements RendererFactory
{
    static void register()
    {
        RenderablesRegister.get().register( Image.class, new ImageRendererFactory() );
    }

    private ImageRendererFactory()
    {
        // prevention
    }

    @Override
    public Renderer create( final Client client, final Context context )
    {
        return new ImageRenderer( client, context );
    }
}
