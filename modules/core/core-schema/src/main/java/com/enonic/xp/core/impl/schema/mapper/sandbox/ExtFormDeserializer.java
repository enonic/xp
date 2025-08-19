package com.enonic.xp.core.impl.schema.mapper.sandbox;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;

public class ExtFormDeserializer
    extends JsonDeserializer<Form>
{
    @Override
    public Form deserialize( final JsonParser jsonParser, final DeserializationContext deserializationContext )
        throws IOException
    {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        FormYml yml = mapper.readValue(jsonParser, FormYml.class);
        return toForm(yml);
    }

    private static Form toForm( FormYml form )
    {
        List<FormItem> formItems = form.formItems.stream().map( item -> {
            if ( item instanceof InputYml input )
            {
                return InputRegistry.toInput( input );
            }
            if ( item instanceof ItemSetYml itemSet )
            {
                return itemSet.toFormItemSet();
            }
            return null;
        } ).filter( Objects::nonNull ).toList();
        return Form.create().addFormItems( formItems ).build();
    }
}
