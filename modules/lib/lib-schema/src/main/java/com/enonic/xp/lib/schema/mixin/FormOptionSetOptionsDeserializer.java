package com.enonic.xp.lib.schema.mixin;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.form.FormOptionSetOption;

public final class FormOptionSetOptionsDeserializer
    extends JsonDeserializer<Iterable<FormOptionSetOption>>
{
    @Override
    public Iterable<FormOptionSetOption> deserialize( final JsonParser jsonParser, final DeserializationContext deserializationContext )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final ArrayNode arr = mapper.readTree( jsonParser );

        for ( JsonNode node : arr )
        {
            if ( node.isObject() && !node.has( "type" ) )
            {
                ( (ObjectNode) node ).put( "type", "OptionSetOption" );
            }
        }

        final JavaType listType = mapper.getTypeFactory().constructCollectionType( List.class, FormOptionSetOption.class );
        return mapper.readValue( mapper.treeAsTokens( arr ), listType );
    }
}
