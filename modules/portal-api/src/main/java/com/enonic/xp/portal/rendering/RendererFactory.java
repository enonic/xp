package com.enonic.xp.portal.rendering;

import com.enonic.wem.api.rendering.Renderable;

public interface RendererFactory
{
    <R extends Renderable> Renderer<R> getRenderer( R renderable );
}
