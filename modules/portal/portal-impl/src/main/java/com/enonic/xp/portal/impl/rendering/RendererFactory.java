package com.enonic.xp.portal.impl.rendering;

public interface RendererFactory
{
    <R> Renderer<R> getRenderer( R renderable );
}
