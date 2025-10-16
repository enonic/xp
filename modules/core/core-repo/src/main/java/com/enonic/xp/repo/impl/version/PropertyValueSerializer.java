package com.enonic.xp.repo.impl.version;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import com.enonic.xp.util.PropertyValue;

class PropertyValueSerializer
    extends JsonSerializer<PropertyValue>
{
    @Override
    public void serialize( PropertyValue value, JsonGenerator gen, SerializerProvider serializers )
        throws IOException
    {
        switch ( value.getType() )
        {
            case STRING -> gen.writeString( value.asString() );
            case NUMBER ->
            {
                gen.writeNumber( value.asDouble() );
            }
            case BOOLEAN -> gen.writeBoolean( value.asBoolean() );
            case LIST ->
            {
                gen.writeStartArray();
                for ( PropertyValue item : value.asList() )
                {
                    serialize( item, gen, serializers );
                }
                gen.writeEndArray();
            }
            case OBJECT ->
            {
                gen.writeStartObject();
                for ( Map.Entry<String, PropertyValue> entry : value.getProperties() )
                {
                    gen.writeFieldName( entry.getKey() );
                    serialize( entry.getValue(), gen, serializers );
                }
                gen.writeEndObject();
            }
        }
    }
}
