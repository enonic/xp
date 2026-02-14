package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.util.GenericValue;

public class PropertyValueDeserializer
    extends JsonDeserializer<GenericValue>
{
    @Override
    public GenericValue deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final JsonNode node = mapper.readTree( jsonParser );

        return fromYml( node );
    }

    public static GenericValue fromYml( final JsonNode node )
    {
        if ( node == null )
        {
            return null;
        }
        else if ( node.isTextual() )
        {
            return GenericValue.stringValue( node.asText() );
        }
        else if ( node.isDouble() )
        {
            return GenericValue.numberValue( node.asDouble() );
        }
        else if ( node.canConvertToLong() )
        {
            return GenericValue.numberValue( node.asLong() );
        }
        else if ( node.isBoolean() )
        {
            return GenericValue.booleanValue( node.asBoolean() );
        }
        else if ( node.isArray() )
        {
            final GenericValue.ListBuilder listBuilder = GenericValue.newList();
            node.valueStream().forEach( item -> listBuilder.add( PropertyValueDeserializer.fromYml( item ) ) );
            return listBuilder.build();
        }
        else if ( node.isObject() )
        {
            final GenericValue.ObjectBuilder objectBuilder = GenericValue.newObject();
            node.propertyStream().forEach( e -> objectBuilder.put( e.getKey(), fromYml( e.getValue() ) ) );
            return objectBuilder.build();
        }
        else
        {
            throw new IllegalArgumentException( "Unknown property type: " + node );
        }
    }
}
