package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.inputtype.InputTypeProperty;

public class InputTypePropertyBuilderDeserializer
    extends JsonDeserializer<InputTypeProperty>
{
    @Override
    public InputTypeProperty deserialize( final JsonParser jsonParser, final DeserializationContext deserializationContext )
        throws IOException
    {
        final ObjectNode node = jsonParser.getCodec().readTree( jsonParser );

        final String name = node.get( "name" ) != null ? node.get( "name" ).asText() : null;
        final String value = node.get( "value" ) != null ? node.get( "value" ).asText() : null;

        final InputTypeProperty.Builder builder = InputTypeProperty.create( name, value );

        if ( node.has( "attributes" ) )
        {
            final ObjectNode attrNode = (ObjectNode) node.get( "attributes" );
            attrNode.properties().forEach( e -> builder.attribute( e.getKey(), e.getValue().asText() ) );
        }

        return builder.build();
    }
}
