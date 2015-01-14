package com.enonic.wem.portal.internal.rendering;

import com.enonic.wem.api.rendering.Renderable;

public interface RendererFactory
{
    <R extends Renderable> Renderer<R> getRenderer( R renderable );
}
