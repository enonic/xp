package com.enonic.wem.core.entity.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.support.ObjectMapperHelper;

public class NodeJsonSerializer
{

    protected static final ObjectMapper objectMapper = ObjectMapperHelper.create();

    public static String toString( final Node node )
    {
        try
        {
            return objectMapper.writeValueAsString( new NodeJson( node ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new NodeJsonSerializerException( "Failed to serialized Node", e );
        }
    }

    public static Node toNode( final String serialized )
    {
        try
        {
            final NodeJson node = objectMapper.readValue( serialized, NodeJson.class );

            return node.getNode();

        }
        catch ( IOException e )
        {
            System.out.println( serialized );
            throw new NodeJsonSerializerException( "Failed to deserialize Node", e );
        }
    }

}
