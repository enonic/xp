package com.enonic.wem.core.content;


import org.codehaus.jackson.JsonNode;

public class JsonParserUtil
{
    public static String getStringValue( String fieldName, JsonNode node, String defaultValue )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            return defaultValue;
        }
        return subNode.getTextValue();
    }

    public static String getStringValue( String fieldName, JsonNode node )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            throw new JsonParsingException( "Field [" + fieldName + "]  does not exist in: " + node.toString() );
        }
        return subNode.getTextValue();
    }


    public static Boolean getBooleanValue( String fieldName, JsonNode node )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            throw new JsonParsingException( "Field [" + fieldName + "]  does not exist in: " + node.toString() );
        }
        return subNode.getBooleanValue();
    }
}
