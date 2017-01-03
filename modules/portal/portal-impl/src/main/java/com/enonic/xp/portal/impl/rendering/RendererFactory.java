package com.enonic.xp.portal.impl.rendering;

import com.google.common.annotations.Beta;

@Beta
public interface RendererFactory
{
    <R> Renderer<R> getRenderer( R renderable );
}
