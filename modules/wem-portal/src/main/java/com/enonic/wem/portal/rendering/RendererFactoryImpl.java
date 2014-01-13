package com.enonic.wem.portal.rendering;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.enonic.wem.api.rendering.Renderable;

final class RendererFactoryImpl
    implements RendererFactory
{
    private final ImmutableMap<Class, Provider<Renderer>> renderers;

    @Inject
    public RendererFactoryImpl( final Map<Class, Provider<Renderer>> renderers )
    {
        this.renderers = ImmutableMap.copyOf( renderers );
    }

    @Override
    public Renderer getRenderer( final Class<? extends Renderable> renderableType )
    {
        return findRenderer( renderableType );
    }

    @Override
    public Renderer getRenderer( final Renderable renderable )
    {
        return getRenderer( renderable.getClass() );
    }

    private synchronized Renderer findRenderer( final Class type )
    {
        final Provider<Renderer> renderer = this.renderers.get( type );
        return renderer != null ? renderer.get() : null;
    }
}
