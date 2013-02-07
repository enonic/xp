package com.enonic.wem.core.support.serializer;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;

public class JsonSerializerUtil
{
    private static DateTimeFormatter isoDateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

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
        return subNode.isNull() ? defaultValue : subNode.getIntValue();
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
        return isoDateTimeFormatter.parseDateTime( subNode.getTextValue() );
    }

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

    public static Boolean getBooleanValue( String fieldName, JsonNode node, Boolean defaultValue )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            return defaultValue;
        }
        return subNode.getBooleanValue();
    }
}
