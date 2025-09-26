package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.inputtype.BooleanPropertyValue;
import com.enonic.xp.inputtype.DoublePropertyValue;
import com.enonic.xp.inputtype.IntegerPropertyValue;
import com.enonic.xp.inputtype.ListPropertyValue;
import com.enonic.xp.inputtype.LongPropertyValue;
import com.enonic.xp.inputtype.ObjectPropertyValue;
import com.enonic.xp.inputtype.PropertyValue;
import com.enonic.xp.inputtype.StringPropertyValue;

public class PropertyValueDeserializer
    extends JsonDeserializer<PropertyValue>
{
    @Override
    public PropertyValue deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final JsonNode node = mapper.readTree( jsonParser );

        return fromYml( node );
    }

    public static PropertyValue fromYml( final JsonNode node )
    {
        if ( node == null )
        {
            return null;
        }
        else if ( node.isTextual() )
        {
            return new StringPropertyValue( node.asText() );
        }
        else if ( node.isDouble() )
        {
            return new DoublePropertyValue( node.asDouble() );
        }
        else if ( node.canConvertToInt() )
        {
            return new IntegerPropertyValue( node.asInt() );
        }
        else if ( node.canConvertToLong() )
        {
            return new LongPropertyValue( node.asLong() );
        }
        else if ( node.isBoolean() )
        {
            return new BooleanPropertyValue( node.asBoolean() );
        }
        else if ( node.isArray() )
        {
            final List<PropertyValue> properties = new ArrayList<>();
            node.forEach( item -> properties.add( fromYml( item ) ) );
            return new ListPropertyValue( properties );
        }
        else if ( node.isObject() )
        {
            final LinkedHashMap<String, PropertyValue> properties = new LinkedHashMap<>();
            node.fieldNames().forEachRemaining( fieldName -> properties.put( fieldName, fromYml( node.get( fieldName ) ) ) );
            return new ObjectPropertyValue( properties );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown property type: " + node );
        }
    }
}
