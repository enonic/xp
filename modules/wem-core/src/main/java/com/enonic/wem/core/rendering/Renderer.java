package com.enonic.wem.core.rendering;


import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Image;
import com.enonic.wem.api.content.page.Layout;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.Part;
import com.enonic.wem.api.content.page.Renderable;

public final class Renderer
{
    private final ConcurrentMap<Class<? extends Renderable>, ComponentRenderer> componentRegister;

    private final Client client;

    public Renderer( final Client client )
    {
        this.client = client;
        this.componentRegister = Maps.newConcurrentMap();
        registerComponentRenderer( Page.class, new PageRenderer( client ) );
        registerComponentRenderer( Part.class, new PartRenderer() );
        registerComponentRenderer( Layout.class, new LayoutRenderer() );
        registerComponentRenderer( Image.class, new ImageRenderer() );
    }

    public RenderingResult render( final Renderable renderable )
    {
        final ComponentRenderer componentRenderer = resolveComponentRenderer( renderable );
        final Context context = new Context();
        final RenderingResult result = componentRenderer.execute( renderable, context );

        return result;
    }

    private ComponentRenderer resolveComponentRenderer( final Renderable renderable )
    {
        return this.componentRegister.get( renderable.getClass() );
    }

    public void registerComponentRenderer( final Class<? extends Renderable> component, final ComponentRenderer componentRenderer )
    {
        this.componentRegister.put( component, componentRenderer );
    }
}
