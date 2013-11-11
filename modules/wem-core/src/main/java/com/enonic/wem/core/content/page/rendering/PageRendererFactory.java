package com.enonic.wem.core.content.page.rendering;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.RenderablesRegister;
import com.enonic.wem.core.rendering.Renderer;
import com.enonic.wem.core.rendering.RendererFactory;

@SuppressWarnings("UnusedDeclaration")
public class PageRendererFactory
    implements RendererFactory
{
    static void register()
    {
        RenderablesRegister.get().register( Page.class, new PageRendererFactory() );
    }

    private PageRendererFactory()
    {
        // prevention
    }

    @Override
    public Renderer create( final Client client, final Context context )
    {
        return new PageRenderer( client, context );
    }
}
