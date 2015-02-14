package com.enonic.xp.support.serializer;

import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.security.PrincipalKey;

public class JsonSerializerUtil
{
    public static PrincipalKey getPrincipalKeyValue( final String propertyName, final JsonNode node )
    {
        final String value = getStringValue( propertyName, node, null );
        return value != null ? PrincipalKey.from( value ) : null;
    }

    public static void setInstantValue( String fieldName, Instant instant, ObjectNode node )
    {
        if ( instant == null )
        {
            node.putNull( fieldName );
        }
        else
        {
            node.put( fieldName, instant.toString() );
        }
    }

    public static Instant getInstantValue( String fieldName, JsonNode node )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            throw new JsonParsingException( "Field [" + fieldName + "]  does not exist in: " + node.toString() );
        }
        else if ( subNode.isNull() )
        {
            return null;
        }
        return Instant.parse( subNode.textValue() );

    }

    public static String getStringValue( String fieldName, JsonNode node, String defaultValue )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            return defaultValue;
        }
        return subNode.textValue();
    }

    public static String getStringValue( String fieldName, JsonNode node )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            throw new JsonParsingException( "Field [" + fieldName + "]  does not exist in: " + node.toString() );
        }
        return subNode.textValue();
    }


    public static Boolean getBooleanValue( String fieldName, JsonNode node )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            throw new JsonParsingException( "Field [" + fieldName + "]  does not exist in: " + node.toString() );
        }
        return subNode.booleanValue();
    }
}
