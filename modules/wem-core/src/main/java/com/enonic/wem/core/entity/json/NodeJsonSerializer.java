package com.enonic.wem.core.entity.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.wem.api.entity.Node;
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
            final AbstractEntityJson entity = objectMapper.readValue( serialized, AbstractEntityJson.class );

            if ( entity instanceof NodeJson )
            {
                return ( (NodeJson) entity ).getNode();
            }
            else
            {
                return null;
            }
        }
        catch ( IOException e )
        {
            throw new NodeJsonSerializerException( "Failed to deserialize Node", e );
        }
    }

}
