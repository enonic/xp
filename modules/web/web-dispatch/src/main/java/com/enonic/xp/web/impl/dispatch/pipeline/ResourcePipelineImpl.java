package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public abstract class ResourcePipelineImpl<T extends ResourceDefinition<?>>
    implements ResourcePipeline<T>
{
    private ServletContext context;

    private final Map<Object, T> map = new ConcurrentHashMap<>();

    final List<T> list = new CopyOnWriteArrayList<>();

    private Optional<String> connector;

    private final List<T> resourceQueue = new CopyOnWriteArrayList<>();

    @Override
    public final void init( final ServletContext context )
        throws ServletException
    {
        this.context = context;
        this.list.forEach( r -> r.init( this.context ) );
    }

    protected void activate( Map<String, Object> properties )
    {
        connector = Optional.ofNullable( (String) properties.get( DispatchConstants.CONNECTOR_PROPERTY ) );

        if ( resourceQueue.size() > 0 )
        {
            resourceQueue.forEach( this::initResource );
            resourceQueue.clear();
        }
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

        if ( this.connector == null )
        {
            resourceQueue.add( def );
            return;
        }

        initResource( def );
    }

    void initResource( final T def )
    {

        if ( !sameConnector( def ) )
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

    protected List<String> getConnectorsFromProperty( final Map<String, ?> props )
    {
        final Object connectorProperty = props.get( DispatchConstants.CONNECTOR_PROPERTY );

        return connectorProperty == null
            ? List.of()
            : connectorProperty instanceof String[] ? List.of( (String[]) connectorProperty ) : List.of( (String) connectorProperty );
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

    boolean sameConnector( final T def )
    {
        final List<String> value = def.getConnectors();

        if ( value.isEmpty() || this.connector.isEmpty() )
        {
            return true;
        }

        return value.contains( this.connector.get() );
    }

}
