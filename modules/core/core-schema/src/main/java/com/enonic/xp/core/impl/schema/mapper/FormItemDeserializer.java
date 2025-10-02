package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;

public class FormItemDeserializer
    extends JsonDeserializer<FormItem>
{
    @Override
    public FormItem deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final JsonNode node = mapper.readTree( jsonParser );

        final String type = node.get( "type" ).asText();

        return switch ( type )
        {
            case "FieldSet" -> mapper.treeToValue( node, FieldSet.class );
            case "ItemSet" -> mapper.treeToValue( node, FormItemSet.class );
            case "OptionSet" -> mapper.treeToValue( node, FormOptionSet.class );
            case "OptionSetOption" -> mapper.treeToValue( node, FormOptionSetOption.class );
            case "FormFragment" -> mapper.treeToValue( node, InlineMixin.class );
            default ->
            {
                final InputYml inputYml = mapper.treeToValue( node, InputRegistry.getInputType( type ) );
                yield inputYml.convertToInput();
            }
        };
    }
}
