package com.enonic.xp.web.impl.dispatch.status;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.ServiceReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.core.internal.concurrent.AtomicSortedList;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public abstract class ResourceStatusReporter<T extends ResourceDefinition>
    implements StatusReporter
{
    private final String name;

    private final Map<Object, T> map = new ConcurrentHashMap<>();

    private final AtomicSortedList<T> list = new AtomicSortedList<>( Comparator.comparingInt( T::getOrder ) );

    ResourceStatusReporter( final String name )
    {
        this.name = name;
    }

    @Override
    public final String getName()
    {
        return this.name;
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    final JsonNode getReport()
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final ResourceDefinition<?> def : this.list.snapshot() )
        {
            final ObjectNode node = json.addObject();
            node.put( "order", def.getOrder() );
            node.put( "name", def.getName() );
            node.put( "class", def.getResource().getClass().getName() );
            node.set( "patterns", getArrayNode( def.getUrlPatterns() ) );
            node.set( "connectors", getArrayNode( def.getConnectors() ) );
            node.set( "initParams", getInitParams( def.getInitParams() ) );
        }

        return json;
    }

    void add( final T def )
    {
        this.map.put( def.getResource(), def );
        this.list.add( def );
    }

    void remove( final Object key )
    {
        final T def = this.map.remove( key );
        if ( def == null )
        {
            return;
        }

        this.list.remove( def );
    }

    private ArrayNode getArrayNode( final Collection<String> values )
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final String value : values )
        {
            json.add( value );
        }

        return json;
    }

    private ObjectNode getInitParams( final Map<String, String> map )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        for ( final Map.Entry<String, String> value : map.entrySet() )
        {
            json.put( value.getKey(), value.getValue() );
        }

        return json;
    }

    protected List<String> getConnectorsFromProperty( ServiceReference<?> serviceReference )
    {
        final Object connectorProperty = serviceReference.getProperty( DispatchConstants.CONNECTOR_PROPERTY );

        return connectorProperty == null
            ? List.of()
            : connectorProperty instanceof String[] ? List.of( (String[]) connectorProperty ) : List.of( (String) connectorProperty );
    }
}
