package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.inputtype.PropertyValue;

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
            return PropertyValue.stringValue( node.asText() );
        }
        else if ( node.isDouble() )
        {
            return PropertyValue.doubleValue( node.asDouble() );
        }
        else if ( node.canConvertToLong() )
        {
            return PropertyValue.longValue( node.asLong() );
        }
        else if ( node.isBoolean() )
        {
            return PropertyValue.booleanValue( node.asBoolean() );
        }
        else if ( node.isArray() )
        {
            return PropertyValue.listValue( node.valueStream().map( PropertyValueDeserializer::fromYml ).toList() );
        }
        else if ( node.isObject() )
        {
            return PropertyValue.objectValue( node.propertyStream()
                                                  .collect(
                                                      Collectors.toMap( Map.Entry::getKey, e -> fromYml( e.getValue() ), ( a, b ) -> a,
                                                                        LinkedHashMap::new ) ) );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown property type: " + node );
        }
    }
}
