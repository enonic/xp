package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;

public class FormBuilderDeserializer
    extends JsonDeserializer<Form>
{
    @Override
    public Form deserialize( final JsonParser jsonParser, final DeserializationContext deserializationContext )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final List<FormItem> items = mapper.readValue( jsonParser, new TypeReference<>()
        {
        } );

        return Form.create().addFormItems( items ).build();
    }
}
