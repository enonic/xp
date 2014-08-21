package com.enonic.wem.portal.internal.rendering;

import com.enonic.wem.api.rendering.Renderable;

public interface RendererFactory
{
    <T extends Renderable> Renderer<T> getRenderer( Class<T> renderableType );

    <T extends Renderable> Renderer<T> getRenderer( T renderable );
}
