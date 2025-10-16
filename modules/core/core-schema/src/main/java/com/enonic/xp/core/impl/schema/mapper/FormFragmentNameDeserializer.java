package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.formfragment.FormFragmentName;

public class FormFragmentNameDeserializer
    extends JsonDeserializer<FormFragmentName>
{
    @Override
    public FormFragmentName deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final JsonNode node = mapper.readTree( jsonParser );

        final String rawValue = node.asText();
        final ApplicationKey currentApplication = (ApplicationKey) ctxt.findInjectableValue( "currentApplication", null, null );

        if ( rawValue.contains( ":" ) )
        {
            if ( !currentApplication.getName().equals( rawValue.split( ":" )[0] ) )
            {
                throw new IllegalStateException( "FormFragment does not belong current application" );
            }
            return FormFragmentName.from( rawValue );
        }
        else
        {
            return FormFragmentName.from( currentApplication, rawValue );
        }
    }
}
