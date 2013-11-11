package com.enonic.wem.core.content.page.rendering;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Part;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.RenderablesRegister;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RendererFactory;

@SuppressWarnings("UnusedDeclaration")
public class PartRendererFactory
    implements RendererFactory
{
    static void register()
    {
        RenderablesRegister.get().register( Part.class, new PartRendererFactory() );
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
