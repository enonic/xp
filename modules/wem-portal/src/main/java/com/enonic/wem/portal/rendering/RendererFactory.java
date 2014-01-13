package com.enonic.wem.portal.rendering;

import com.enonic.wem.api.rendering.Renderable;

public interface RendererFactory
{
    Renderer getRenderer( Class<? extends Renderable> renderableType );

    Renderer getRenderer( Renderable renderable );
}
