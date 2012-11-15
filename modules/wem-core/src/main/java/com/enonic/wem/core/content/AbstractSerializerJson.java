package com.enonic.wem.core.content;


import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class AbstractSerializerJson<T>
{
    private boolean prettyPrint = false;

    public void prettyPrint()
    {
        prettyPrint = true;
    }

    public String toString( T obj )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode jsonNode = serialize( obj, mapper );
        if ( prettyPrint )
        {
            try
            {
                return mapper.defaultPrettyPrintingWriter().writeValueAsString( jsonNode );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        }
        else
        {
            return jsonNode.toString();
        }
    }

    public T toObject( final String json )
    {
        try
        {
            final JsonFactory f = JsonFactoryHolder.DEFAULT_FACTORY;
            final JsonParser jp = f.createJsonParser( json );

            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode node = mapper.readValue( jp, JsonNode.class );

            try
            {
                return parse( node );
            }
            finally
            {
                jp.close();
            }

        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read json", e );
        }
    }

    protected abstract JsonNode serialize( final T obj, final ObjectMapper objectMapper );

    protected abstract T parse( final JsonNode node );

}
