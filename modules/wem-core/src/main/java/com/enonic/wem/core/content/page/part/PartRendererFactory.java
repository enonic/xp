package com.enonic.wem.core.content.page.part;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.RenderablesRegister;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RendererFactory;

@SuppressWarnings("UnusedDeclaration")
public class PartRendererFactory
    implements RendererFactory
{
    public static void register()
    {
        RenderablesRegister.get().register( PartComponent.class, new PartRendererFactory() );
    }

    private PartRendererFactory()
    {
        // prevention
    }

    @Override
    public Renderer create( final Client client, final Context context )
    {
        return new PartRenderer( client, context );
    }
}
