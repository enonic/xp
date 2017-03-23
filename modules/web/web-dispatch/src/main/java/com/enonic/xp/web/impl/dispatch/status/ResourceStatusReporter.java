package com.enonic.xp.web.impl.dispatch.status;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public abstract class ResourceStatusReporter
    extends JsonStatusReporter
{
    private final String name;

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
            node.set( "patterns", getPatterns( def.getUrlPatterns() ) );
            node.set( "initParams", getInitParams( def.getInitParams() ) );
        }

        return json;
    }

    private ArrayNode getPatterns( final Set<String> values )
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

    abstract Iterable<? extends ResourceDefinition> getDefinitions();
}
