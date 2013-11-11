package com.enonic.wem.core.rendering;


import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import com.enonic.wem.api.rendering.Renderable;

public class RenderablesRegister
{
    private static final RenderablesRegister INSTANCE = new RenderablesRegister();

    private final ConcurrentMap<Class<? extends Renderable>, RendererFactory> renderableFactoryByClass;

    public static RenderablesRegister get()
    {
        return INSTANCE;
    }

    public RenderablesRegister()
    {
        this.renderableFactoryByClass = Maps.newConcurrentMap();
    }

    public void register( final Class<? extends Renderable> renderable, final RendererFactory rendererFactory )
    {
        renderableFactoryByClass.put( renderable, rendererFactory );
    }

    public RendererFactory get( final Class<? extends Renderable> renderable )
    {
        return renderableFactoryByClass.get( renderable );
    }
}
