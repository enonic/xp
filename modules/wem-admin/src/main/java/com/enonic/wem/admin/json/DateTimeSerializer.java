package com.enonic.wem.admin.json;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


public class DateTimeSerializer
    extends StdSerializer<DateTime>
{

    public DateTimeSerializer()
    {
        super( DateTime.class );
    }

    @Override
    public void serialize( DateTime value, JsonGenerator jgen, SerializerProvider provider )
        throws IOException, JsonGenerationException
    {
        if ( provider.isEnabled( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS ) )
        {
            jgen.writeNumber( value.getMillis() );
        }
        else
        {
            jgen.writeString( DateTimeFormatter.format( value ) );
        }
    }

    @Override
    public void serializeWithType( DateTime value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer )
        throws IOException, JsonProcessingException
    {
        typeSer.writeTypePrefixForScalar( value, jgen );
        serialize( value, jgen, provider );
        typeSer.writeTypeSuffixForScalar( value, jgen );
    }

    @Override
    public JsonNode getSchema( SerializerProvider provider, java.lang.reflect.Type typeHint )
    {
        return createSchemaNode( provider.isEnabled( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS ) ? "number" : "string", true );
    }
}
