package com.enonic.xp.web.impl.dispatch.status;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.ServiceReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public abstract class ResourceStatusReporter<T extends ResourceDefinition>
    extends JsonStatusReporter
{
    private final String name;

    private final Map<Object, T> map = new ConcurrentHashMap<>();

    private final List<T> list = new CopyOnWriteArrayList<>();

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
    public final JsonNode getReport()
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final ResourceDefinition<?> def : getDefinitions() )
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

        this.list.sort( Comparator.comparingInt( T::getOrder ) );
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

    Iterable<? extends ResourceDefinition> getDefinitions()
    {
        return this.list;
    }
}
