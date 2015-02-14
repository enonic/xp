package com.enonic.xp.portal.impl.rendering;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.xp.rendering.Renderable;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;

@Component
public final class RendererFactoryImpl
    implements RendererFactory
{
    private final Map<Class, Renderer> renderers;

    public RendererFactoryImpl()
    {
        this.renderers = Maps.newHashMap();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Renderable> Renderer<R> getRenderer( final R renderable )
    {
        return getRenderer( (Class<R>) renderable.getClass() );
    }

    private <R extends Renderable> Renderer<R> getRenderer( final Class<R> renderableType )
    {
        return findRenderer( renderableType );
    }

    @SuppressWarnings("unchecked")
    private <R extends Renderable> Renderer<R> findRenderer( final Class<R> type )
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

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addRenderer( final Renderer renderer )
    {
        this.renderers.put( renderer.getType(), renderer );
    }

    public void removeRenderer( final Renderer renderer )
    {
        this.renderers.remove( renderer.getType() );
    }
}
