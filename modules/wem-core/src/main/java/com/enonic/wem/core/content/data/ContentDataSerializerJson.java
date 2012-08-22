package com.enonic.wem.core.content.data;


import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.core.content.JsonFactoryHolder;
import com.enonic.wem.core.content.type.configitem.ConfigItems;

public class ContentDataSerializerJson
{
    public ContentDataSerializerJson()
    {

    }

    public static String toJson( ContentData contentData )
    {
        try
        {
            StringWriter sw = new StringWriter();
            JsonGenerator g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
            g.useDefaultPrettyPrinter();
            EntriesSerializerJson.generate( contentData.getDataSet(), g );
            g.close();
            sw.close();
            return sw.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to generate json", e );
        }
    }

    public ContentData parse( final String json, final ConfigItems configItems )
    {
        try
        {
            final JsonFactory f = JsonFactoryHolder.DEFAULT_FACTORY;
            final JsonParser jp = f.createJsonParser( json );

            ObjectMapper mapper = new ObjectMapper();
            final JsonNode contentDataNode = mapper.readValue( jp, JsonNode.class );

            DataSet dataSet = EntriesSerializerJson.parse( contentDataNode, configItems );
            ContentData contentData;
            if ( configItems == null )
            {
                contentData = new ContentData();
            }
            else
            {
                contentData = new ContentData( configItems );
            }
            contentData.setDataSet( dataSet );

            jp.close();

            return contentData;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read json", e );
        }
    }
}
