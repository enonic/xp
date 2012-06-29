package com.enonic.wem.core.content.config.field.type;


import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class FieldTypeParser
{
    public static FieldType parse( final JsonParser jp )
        throws IOException
    {
        String className = null;
        String name = null;
        String valueType = null;

        JsonToken token = jp.nextToken();
        while ( token != JsonToken.END_OBJECT )
        {

            if ( "className".equals( jp.getCurrentName() ) && token == JsonToken.FIELD_NAME )
            {
                jp.nextToken();
                className = jp.getText();
            }
            else if ( "name".equals( jp.getCurrentName() ) && token == JsonToken.FIELD_NAME )
            {
                jp.nextToken();
                name = jp.getText();
            }
            else if ( "valueType".equals( jp.getCurrentName() ) && token == JsonToken.FIELD_NAME )
            {
                jp.nextToken();
                valueType = jp.getText();
            }
            token = jp.nextToken();
        }

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
