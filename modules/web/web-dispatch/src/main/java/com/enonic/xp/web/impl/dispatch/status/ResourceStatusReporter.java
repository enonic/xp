package com.enonic.xp.web.impl.dispatch.status;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;
import com.enonic.xp.web.impl.dispatch.pipeline.ResourcePipeline;

public abstract class ResourceStatusReporter
    implements StatusReporter
{
    private final String name;

    private final ResourcePipeline<?> resourcePipeline;

    ResourceStatusReporter( final String name, final ResourcePipeline<?> resourcePipeline )
    {
        this.name = name;
        this.resourcePipeline = resourcePipeline;
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

    private JsonNode getReport()
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final ResourceDefinition<?> def : this.resourcePipeline.list() )
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
}
