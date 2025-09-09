package com.enonic.xp.core.impl.content.parser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;

final class InputTypeConfigDeserializer
    extends JsonDeserializer<InputTypeConfig>
{
    @Override
    public InputTypeConfig deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final JsonNode node = mapper.readTree( jsonParser );

        final InputTypeConfig.Builder builder = InputTypeConfig.create();

        for ( JsonNode property : node )
        {
            final String name = property.get( "name" ).asText();
            final String value = property.get( "value" ).asText();

            final InputTypeProperty.Builder propertyBuilder = InputTypeProperty.create( name, value );

            property.fieldNames().forEachRemaining( attr -> {
                if ( "value".equals( attr ) || "name".equals( attr ) )
                {
                    return;
                }
                propertyBuilder.attribute( attr, property.get( attr ).asText() );
            } );

            builder.property( propertyBuilder.build() );
        }

        return builder.build();
    }
}
