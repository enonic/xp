package com.enonic.xp.portal.rendering;

import com.google.common.annotations.Beta;

import com.enonic.xp.rendering.Renderable;

@Beta
public interface RendererFactory
{
    <R extends Renderable> Renderer<R> getRenderer( R renderable );
}
