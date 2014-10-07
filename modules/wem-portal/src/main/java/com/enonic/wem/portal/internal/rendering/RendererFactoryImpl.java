package com.enonic.wem.portal.internal.rendering;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.PortalContext;

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

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Renderable, CONTEXT extends PortalContext> Renderer<T, CONTEXT> getRenderer( final T renderable )
    {
        return getRenderer( (Class<T>) renderable.getClass() );
    }

    private <T extends Renderable, CONTEXT extends PortalContext> Renderer<T, CONTEXT> getRenderer( final Class<T> renderableType )
    {
        return findRenderer( renderableType );
    }

    @SuppressWarnings("unchecked")
    private <T extends Renderable, CONTEXT extends PortalContext> Renderer<T, CONTEXT> findRenderer( final Class<T> type )
    {
        final Renderer renderer = doResolveRenderer( type );
        if ( renderer == null )
        {
            throw new RendererNotFoundException( type );
        }
        return renderer;
    }

    private Renderer doResolveRenderer( Class type )
    {
        Renderer renderer = this.renderers.get( type );
        if ( renderer == null )
        {
            if ( type.getSuperclass().equals( Object.class ) )
            {
                return null;
            }
            else
            {
                return findRenderer( type.getSuperclass() );
            }
        }
        else
        {
            return renderer;
        }
    }
}
