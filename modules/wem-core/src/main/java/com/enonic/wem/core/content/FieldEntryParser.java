package com.enonic.wem.core.content;


import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.FieldJsonParser;

public class FieldEntryParser
{
    public static FieldEntry parse( JsonParser jp, ConfigItems configItems )
        throws IOException
    {
        FieldValue.Builder builder = FieldValue.newBuilder();

        JsonToken token = jp.nextToken();
        while ( token != JsonToken.END_OBJECT )
        {
            if ( token == JsonToken.FIELD_NAME && "value".equals( jp.getCurrentName() ) )
            {
                jp.nextToken();
                String value = jp.getText();
                builder.value( value );
            }

            if ( token == JsonToken.FIELD_NAME && "field".equals( jp.getCurrentName() ) )
            {
                jp.nextToken();
                Field field = FieldJsonParser.parse( jp, configItems );
                builder.field( field );
            }
            token = jp.nextToken();
        }

        return builder.build();
    }
}
