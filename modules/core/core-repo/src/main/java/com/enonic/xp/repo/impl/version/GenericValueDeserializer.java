package com.enonic.xp.repo.impl.version;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.enonic.xp.util.GenericValue;

class GenericValueDeserializer
    extends JsonDeserializer<GenericValue>
{
    private static ObjectMapper MAPPER = new ObjectMapper();
    static
    {

        SimpleModule module = new SimpleModule();
        module.addDeserializer( GenericValue.class, new GenericValueDeserializer() );

        MAPPER.registerModule( module );
    }

    static GenericValue deserialize(String json) {
        try
        {
            return MAPPER.readValue( json, GenericValue.class );
        }
        catch ( JsonProcessingException e )
        {
            throw new UncheckedIOException( e );
        }
    }


    @Override
    public GenericValue deserialize( JsonParser p, DeserializationContext ctxt )
        throws IOException
    {
        JsonToken token = p.currentToken();
        if ( token == null )
        {
            token = p.nextToken();
        }
        switch ( token )
        {
            case VALUE_STRING:
                return GenericValue.stringValue( p.getText() );
            case VALUE_NUMBER_INT:
                return GenericValue.numberValue( p.getLongValue() );
            case VALUE_NUMBER_FLOAT:
                return GenericValue.numberValue( p.getDoubleValue() );
            case VALUE_TRUE:
            case VALUE_FALSE:
                return GenericValue.booleanValue( p.getBooleanValue() );
            case START_ARRAY:
            {
                var list = GenericValue.list();
                while ( p.nextToken() != JsonToken.END_ARRAY )
                {
                    list.add( deserialize( p, ctxt ) );
                }
                return list.build();
            }
            case START_OBJECT:
            {
                var obj = GenericValue.object();
                while ( p.nextToken() != JsonToken.END_OBJECT )
                {
                    String fieldName = p.currentName();
                    p.nextToken();
                    obj.put( fieldName, deserialize( p, ctxt ) );
                }
                return obj.build();
            }
            default:
                throw ctxt.wrongTokenException( p, GenericValue.class, token, "" );
        }
    }
}
