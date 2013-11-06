package com.enonic.wem.api.support.serializer;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;

public class JsonSerializerUtil
{
    public static final DateTimeFormatter isoDateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

    public static UserKey getUserKeyValue( final String propertyName, final JsonNode node )
    {
        final String value = getStringValue( propertyName, node, null );
        return value != null ? AccountKey.from( value ).asUser() : null;
    }

    public static Integer getIntegerValue( String fieldName, JsonNode node, Integer defaultValue )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            return defaultValue;
        }
        return subNode.isNull() ? defaultValue : subNode.intValue();
    }

    public static void setDateTimeValue( String fieldName, DateTime dateTime, ObjectNode node )
    {
        if ( dateTime == null )
        {
            node.putNull( fieldName );
        }
        else
        {
            node.put( fieldName, isoDateTimeFormatter.print( dateTime ) );
        }
    }

    public static DateTime getDateTimeValue( String fieldName, JsonNode node )
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
        return isoDateTimeFormatter.parseDateTime( subNode.textValue() );
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

    public static Boolean getBooleanValue( String fieldName, JsonNode node, Boolean defaultValue )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            return defaultValue;
        }
        return subNode.booleanValue();
    }
}
