package com.enonic.wem.portal.internal.rendering;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.enonic.wem.api.rendering.Renderable;

final class RendererFactoryImpl
    implements RendererFactory
{
    private final ImmutableMap<Class<? extends Renderable>, Provider<Renderer>> renderers;

    @Inject
    public RendererFactoryImpl( final Map<Class<? extends Renderable>, Provider<Renderer>> renderers )
    {
        this.renderers = ImmutableMap.copyOf( renderers );
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
        final Provider<? extends Renderer> renderer = this.renderers.get( type );
        return renderer != null ? renderer.get() : null;
    }
}
