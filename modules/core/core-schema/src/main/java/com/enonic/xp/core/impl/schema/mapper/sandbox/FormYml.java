package com.enonic.xp.core.impl.schema.mapper.sandbox;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FormYml
{
    public List<FormItemYml> formItems;

    public static class FormYmlDeserializer
        extends JsonDeserializer<FormYml>
    {
        @Override
        public FormYml deserialize( final JsonParser jsonParser, final DeserializationContext deserializationContext )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            final List<FormItemYml> items = mapper.readValue( jsonParser, new TypeReference<>()
            {
            } );

            FormYml formYml = new FormYml();
            formYml.formItems = items;
            return formYml;
        }
    }
}
