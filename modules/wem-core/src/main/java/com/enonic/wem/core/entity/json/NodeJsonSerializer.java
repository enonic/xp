package com.enonic.wem.core.entity.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.support.ObjectMapperHelper;

public final class NodeJsonSerializer
{
    protected static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    public static String toString( final Node node )
    {
        try
        {
            return MAPPER.writeValueAsString( new NodeJson( node ) );
        }
        catch ( final JsonProcessingException e )
        {
            throw new NodeJsonSerializerException( "Failed to serialized Node", e );
        }
    }

    public static Node toNode( final String serialized )
    {
        try
        {
            final NodeJson node = MAPPER.readValue( serialized, NodeJson.class );
            return node.getNode();
        }
        catch ( final IOException e )
        {
            throw new NodeJsonSerializerException( "Failed to deserialize Node", e );
        }
    }
}
