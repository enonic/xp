package com.enonic.wem.core.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.rendering.Renderable;

public final class CoreRenderer
{
    private final Client client;

    public CoreRenderer( final Client client )
    {
        this.client = client;
    }

    public RenderingResult render( final Renderable renderable )
    {
        final Context context = new Context();

        final RendererFactory rendererFactory = RenderablesRegister.get().get( renderable.getClass() );
        final Renderer renderer = rendererFactory.create( this.client, context );
        final RenderingResult result = renderer.execute( renderable );
        return result;
    }
}
