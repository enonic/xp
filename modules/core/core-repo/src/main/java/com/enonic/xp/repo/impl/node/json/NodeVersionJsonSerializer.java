package com.enonic.xp.repo.impl.node.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.util.Exceptions;

public final class NodeVersionJsonSerializer
{
    protected final ObjectMapper mapper;

    private NodeVersionJsonSerializer( final ObjectMapper mapper )
    {
        this.mapper = mapper;
    }

    public String toString( final NodeVersion nodeVersion )
    {
        try
        {
            return this.mapper.writeValueAsString( NodeVersionJson.toJson( nodeVersion ) );

        }
        catch ( final JsonProcessingException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public NodeVersion toNodeVersion( final String serialized )
    {
        try
        {
            final NodeVersionJson nodeVersionJson = this.mapper.readValue( serialized, NodeVersionJson.class );

            return nodeVersionJson.fromJson();
        }
        catch ( final IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public static NodeVersionJsonSerializer create( final boolean indent )
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        if ( indent )
        {
            mapper.enable( SerializationFeature.INDENT_OUTPUT );
        }

        return new NodeVersionJsonSerializer( mapper );
    }
}
