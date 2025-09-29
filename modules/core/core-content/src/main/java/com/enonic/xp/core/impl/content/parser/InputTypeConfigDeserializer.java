package com.enonic.xp.core.impl.content.parser;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.PropertyValue;

final class InputTypeConfigDeserializer
    extends JsonDeserializer<InputTypeConfig>
{
    @Override
    public InputTypeConfig deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final LinkedHashMap<String, PropertyValue> configNode = mapper.readValue( jsonParser, new TypeReference<>()
        {
        } );

        final InputTypeConfig.Builder builder = InputTypeConfig.create();
        configNode.forEach( ( name, value ) -> builder.property( InputTypeProperty.create( name, value ).build() ) );
        return builder.build();
    }
}
