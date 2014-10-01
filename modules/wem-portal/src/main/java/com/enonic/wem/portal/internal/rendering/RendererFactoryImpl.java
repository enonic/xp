package com.enonic.wem.portal.internal.rendering;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.rendering.Renderable;

public final class RendererFactoryImpl
    implements RendererFactory
{
    private final Map<Class, Renderer> renderers;

    public RendererFactoryImpl( final List<Renderer> renderers )
    {
        this.renderers = Maps.newHashMap();

        for ( final Renderer renderer : renderers )
        {
            this.renderers.put( renderer.getType(), renderer );
        }
    }

    private <T extends Renderable> Renderer<T> getRenderer( final Class<T> renderableType )
    {
        return findRenderer( renderableType );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Renderable> Renderer<T> getRenderer( final T renderable )
    {
        return getRenderer( (Class<T>) renderable.getClass() );
    }

    @SuppressWarnings("unchecked")
    private <T extends Renderable> Renderer<T> findRenderer( final Class<T> type )
    {
        return this.renderers.get( type );
    }
}
