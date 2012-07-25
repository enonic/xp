package com.enonic.wem.core.content.type.configitem.field.type;


import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import com.enonic.wem.core.content.JsonParserUtil;

public class FieldTypeJsonParser
{
    public static FieldType parse( final JsonNode node )
        throws IOException
    {
        String className = JsonParserUtil.getStringValue( "className", node );

        return instantiate( className );
    }

    public static FieldType parse( final JsonParser jp )
        throws IOException
    {
        String className = null;

        JsonToken token = jp.nextToken();
        while ( token != JsonToken.END_OBJECT )
        {
            if ( "className".equals( jp.getCurrentName() ) && token == JsonToken.FIELD_NAME )
            {
                jp.nextToken();
                className = jp.getText();
            }
            token = jp.nextToken();
        }

        return instantiate( className );

    }

    private static FieldType instantiate( final String className )
    {
        Class clazz;
        try
        {
            clazz = Class.forName( className );
            return (FieldType) clazz.newInstance();
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( e );
        }
        catch ( InstantiationException e )
        {
            throw new RuntimeException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }
}
