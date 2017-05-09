package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public abstract class ResourcePipelineImpl<T extends ResourceDefinition<?>>
    implements ResourcePipeline<T>
{
    private ServletContext context;

    private final Map<Object, T> map;

    final List<T> list;

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

    final void add( final T def )
    {
        if ( def == null )
        {
            return;
        }

        this.map.put( def.getResource(), def );
        this.list.add( def );
        sortList();

        if ( this.context != null )
        {
            def.init( this.context );
        }
    }

    final void remove( final Object key )
    {
        final T def = this.map.remove( key );
        if ( def == null )
        {
            return;
        }

        this.list.remove( def );
        def.destroy();
    }

    @Override
    public final Iterator<T> iterator()
    {
        return this.list.iterator();
    }

    private void sortList()
    {
        this.list.sort( this::compare );
    }

    private int compare( final T def1, final T def2 )
    {
        return def1.getOrder() - def2.getOrder();
    }
}
