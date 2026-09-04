package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;

import com.enonic.xp.core.internal.concurrent.AtomicSortedList;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

import static java.util.Objects.requireNonNull;

public abstract class ResourcePipelineImpl<T extends ResourceDefinition<?>>
    implements ResourcePipeline<T>
{
    /**
     * Guards {@link #context} and {@link #map}, and serializes the lifecycle calls made on definitions.
     * Definitions are added to {@link #list} only after they have been initialized and removed from it
     * before they are destroyed, so request threads never observe a definition that is not ready to serve.
     */
    private final Object lock = new Object();

    private final Map<Object, T> map = new HashMap<>();

    final AtomicSortedList<T> list = new AtomicSortedList<>( Comparator.comparingInt( T::getOrder ) );

    private ServletContext context;

    private final String connector;

    public ResourcePipelineImpl( final Map<String, ?> properties )
    {
        final String connectorValue = (String) properties.get( DispatchConstants.CONNECTOR_PROPERTY );
        this.connector = requireNonNull( connectorValue, "Connector property must not be null" );
    }

    @Override
    public final void init( final ServletContext context )
    {
        requireNonNull( context );

        synchronized ( this.lock )
        {
            this.context = context;
            this.list.snapshot().forEach( def -> def.init( context ) );
        }
    }

    public List<T> list()
    {
        return this.list.snapshot();
    }

    @Override
    public final void destroy()
    {
        synchronized ( this.lock )
        {
            this.context = null;
            this.list.snapshot().forEach( ResourceDefinition::destroy );
        }
    }

    final void add( final T def )
    {
        if ( def == null || !sameConnector( def ) )
        {
            return;
        }

        synchronized ( this.lock )
        {
            if ( this.context != null )
            {
                def.init( this.context );
            }

            this.map.put( def.getResource(), def );
            this.list.add( def );
        }
    }

    final void remove( final Object key )
    {
        synchronized ( this.lock )
        {
            final T def = this.map.remove( key );
            if ( def == null )
            {
                return;
            }

            this.list.remove( def );
            def.destroy();
        }
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
