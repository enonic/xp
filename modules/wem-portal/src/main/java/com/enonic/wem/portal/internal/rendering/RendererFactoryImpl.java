package com.enonic.wem.portal.internal.rendering;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import com.enonic.wem.api.rendering.Renderable;

public final class RendererFactoryImpl
    implements RendererFactory
{
    private final Map<Class, Renderer> renderers;

    @Inject
    public RendererFactoryImpl( final Set<Renderer> renderers )
    {
        this.renderers = Maps.newHashMap();

        for ( final Renderer renderer : renderers )
        {
            this.renderers.put( renderer.getType(), renderer );
        }
    }

    @Override
    public <T extends Renderable> Renderer<T> getRenderer( final Class<T> renderableType )
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
