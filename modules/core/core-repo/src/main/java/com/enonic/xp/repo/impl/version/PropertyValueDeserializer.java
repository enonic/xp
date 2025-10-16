package com.enonic.xp.repo.impl.version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.enonic.xp.util.PropertyValue;

class PropertyValueDeserializer
    extends JsonDeserializer<PropertyValue>
{
    @Override
    public PropertyValue deserialize( JsonParser p, DeserializationContext ctxt )
        throws IOException
    {
        JsonToken token = p.currentToken();
        if ( token == null )
        {
            token = p.nextToken();
        }
        switch ( token )
        {
            case VALUE_STRING:
                return PropertyValue.stringValue( p.getText() );
            case VALUE_NUMBER_INT:
                return PropertyValue.longValue( p.getLongValue() );
            case VALUE_NUMBER_FLOAT:
                return PropertyValue.doubleValue( p.getDoubleValue() );
            case VALUE_TRUE:
            case VALUE_FALSE:
                return PropertyValue.booleanValue( p.getBooleanValue() );
            case START_ARRAY:
            {
                List<PropertyValue> list = new ArrayList<>();
                while ( p.nextToken() != JsonToken.END_ARRAY )
                {
                    list.add( deserialize( p, ctxt ) );
                }
                return PropertyValue.listValue( list );
            }
            case START_OBJECT:
            {
                Map<String, PropertyValue> map = new LinkedHashMap<>();
                while ( p.nextToken() != JsonToken.END_OBJECT )
                {
                    String fieldName = p.getCurrentName();
                    p.nextToken();
                    map.put( fieldName, deserialize( p, ctxt ) );
                }
                return PropertyValue.objectValue( map );
            }
            default:
                throw ctxt.wrongTokenException( p, PropertyValue.class, token, "" );
        }
    }
}