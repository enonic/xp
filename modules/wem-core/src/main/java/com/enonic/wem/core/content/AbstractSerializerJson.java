package com.enonic.wem.core.content;


import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
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
        try
        {
            StringWriter sw = new StringWriter();
            JsonGenerator g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
            if ( prettyPrint )
            {
                g.useDefaultPrettyPrinter();
            }

            g.writeStartObject();
            generate( obj, g );
            g.writeEndObject();

            g.close();
            sw.close();
            return sw.toString();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to generate json", e );
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

    public abstract void generate( final T obj, final JsonGenerator g )
        throws IOException;

    public abstract T parse( final JsonNode node )
        throws IOException;


}
