package com.enonic.wem.core.content;


import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import com.enonic.wem.core.content.config.field.ConfigItems;

public class ContentDataJsonGenerator
{
    public ContentDataJsonGenerator()
    {

    }

    public static String toJson( ContentData contentData )
    {
        try
        {
            StringWriter sw = new StringWriter();
            JsonGenerator g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
            g.useDefaultPrettyPrinter();
            g.writeStartObject();
            FieldEntriesJsonGenerator.generate( contentData.getFieldEntries(), g );
            g.writeEndObject();
            g.close();
            sw.close();
            return sw.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to generate json", e );
        }
    }

    public ContentData toContentData( final String json, final ConfigItems configItems )
    {
        try
        {
            final JsonFactory f = JsonFactoryHolder.DEFAULT_FACTORY;
            final JsonParser jp = f.createJsonParser( json );

            final ContentData contentData = new ContentData( configItems );
            JsonToken token = jp.nextToken();
            while ( token != JsonToken.END_OBJECT )
            {
                if ( "fieldEntries".equals( jp.getCurrentName() ) && token == JsonToken.START_ARRAY )
                {
                    FieldEntries fieldEntries = FieldEntriesParser.parse( jp, configItems );
                    contentData.setFieldEntries( fieldEntries );
                }

                token = jp.nextToken();
            }

            jp.close();

            return contentData;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read json", e );
        }
    }
}
