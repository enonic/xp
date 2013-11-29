package com.enonic.wem.core.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.rendering.Renderable;

public final class CoreRenderer
{
    private final Client client;

    private final Context context;

    public CoreRenderer( final Client client, final Context context )
    {
        this.client = client;
        this.context = context;
    }

    public RenderingResult render( final Renderable renderable )
    {
        final RendererFactory rendererFactory = RenderablesRegister.get().get( renderable.getClass() );
        final Renderer renderer = rendererFactory.create( this.client, context );
        final RenderingResult result = renderer.execute( renderable );
        return result;
    }
}
