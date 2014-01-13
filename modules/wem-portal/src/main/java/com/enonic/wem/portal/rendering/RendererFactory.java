package com.enonic.wem.portal.rendering;

import com.google.inject.ImplementedBy;

import com.enonic.wem.api.rendering.Renderable;

@ImplementedBy(RendererFactoryImpl.class)
public interface RendererFactory
{
    Renderer getRenderer( Class<? extends Renderable> renderableType );

    Renderer getRenderer( Renderable renderable );
}
