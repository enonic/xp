package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public abstract class ResourcePipelineImpl<R, D extends ResourceDefinition<R>>
    implements ResourcePipeline<D>
{
    private ServletContext context;

    private final Map<R, D> map;

    final List<D> list;

    ResourcePipelineImpl()
    {
        this.map = Maps.newHashMap();
        this.list = Lists.newCopyOnWriteArrayList();
    }

    @Override
    public final void init( final ServletContext context )
        throws ServletException
    {
        this.context = context;
        this.list.forEach( r -> r.init( this.context ) );
    }

    @Override
    public final void destroy()
    {
        this.list.forEach( ResourceDefinition::destroy );
    }

    final void add( final R resource, final D def, final Map<String, Object> serviceProps )
    {
        def.configure( serviceProps );
        if ( !def.isValid() )
        {
            return;
        }

        this.map.put( resource, def );
        this.list.add( def );
        sortList();

        if ( this.context != null )
        {
            def.init( this.context );
        }
    }

    final void remove( final R resource )
    {
        final D def = this.map.remove( resource );
        if ( def == null )
        {
            return;
        }

        this.list.remove( def );
        def.destroy();
    }

    @Override
    public final Iterator<D> iterator()
    {
        return this.list.iterator();
    }

    private void sortList()
    {
        this.list.sort( this::compare );
    }

    private int compare( final D def1, final D def2 )
    {
        return def2.getRanking() - def1.getRanking();
    }
}
