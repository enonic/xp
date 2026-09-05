package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.enonic.xp.core.internal.concurrent.AtomicSortedList;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

import static java.util.Objects.requireNonNull;

/**
 * Holds the definitions of one connector, ordered by {@link ResourceDefinition#getOrder()}.
 */
public abstract class ResourcePipelineImpl<T extends ResourceDefinition<?>>
    implements ResourcePipeline<T>
{
    final AtomicSortedList<T> list = new AtomicSortedList<>( Comparator.comparingInt( T::getOrder ) );

    private final String connector;

    public ResourcePipelineImpl( final Map<String, ?> properties )
    {
        final String connectorValue = (String) properties.get( DispatchConstants.CONNECTOR_PROPERTY );
        this.connector = requireNonNull( connectorValue, "Connector property must not be null" );
    }

    @Override
    public List<T> list()
    {
        return this.list.snapshot();
    }

    final void add( final T def )
    {
        if ( def == null || !sameConnector( def ) )
        {
            return;
        }

        this.list.add( def );
    }

    final void remove( final Object resource )
    {
        this.list.removeIf( def -> def.getResource() == resource );
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
