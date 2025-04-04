package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import com.enonic.xp.core.internal.concurrent.AtomicSortedList;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public abstract class ResourcePipelineImpl<T extends ResourceDefinition<?>>
    implements ResourcePipeline<T>
{
    private volatile ServletContext context;

    private final Map<Object, T> map = new ConcurrentHashMap<>();

    final AtomicSortedList<T> list = new AtomicSortedList<>( Comparator.comparingInt( T::getOrder ) );

    private final String connector;

    public ResourcePipelineImpl( final Map<String, ?> properties )
    {
        final String connectorValue = (String) properties.get( DispatchConstants.CONNECTOR_PROPERTY );
        this.connector = Objects.requireNonNull( connectorValue, "Connector property must not be null" );
    }

    @Override
    public final void init( final ServletContext context )
        throws ServletException
    {
        this.context = Objects.requireNonNull( context );
        this.list.snapshot().forEach( r -> r.init( this.context ) );
    }

    public List<T> list()
    {
        return this.list.snapshot();
    }

    @Override
    public final void destroy()
    {
        this.list.snapshot().forEach( ResourceDefinition::destroy );
    }

    final void add( final T def )
    {
        if ( def == null || !sameConnector( def ) )
        {
            return;
        }

        this.map.put( def.getResource(), def );
        this.list.add( def );

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

    protected final List<String> getConnectorsFromProperty( final Map<String, ?> props )
    {
        final Object connectorProperty = props.get( DispatchConstants.CONNECTOR_PROPERTY );

        return connectorProperty == null
            ? List.of()
            : connectorProperty instanceof String[] ? List.of( (String[]) connectorProperty ) : List.of( (String) connectorProperty );
    }

    private boolean sameConnector( final T def )
    {
        final List<String> connectors = def.getConnectors();
        return connectors.isEmpty() || connectors.contains( this.connector );
    }
}
