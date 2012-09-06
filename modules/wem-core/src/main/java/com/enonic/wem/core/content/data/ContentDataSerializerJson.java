package com.enonic.wem.core.content.data;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.type.ContentType;

public class ContentDataSerializerJson
{
    private DataSetSerializerJson dataSetSerializer = new DataSetSerializerJson();

    public ContentDataSerializerJson()
    {

    }

    public void generate( ContentData contentData, JsonGenerator g )
        throws IOException
    {
        g.writeObjectFieldStart( "data" );
        dataSetSerializer.generate( contentData.getDataSet(), g, false );
        g.writeEndObject();
    }

    public ContentData parse( final JsonNode contentNode, final ContentType contentType )
    {
        final JsonNode contentDataNode = contentNode.get( "data" );

        ContentData contentData;
        if ( contentType == null )
        {
            contentData = new ContentData();
            contentData.setDataSet( dataSetSerializer.parse( contentDataNode, null ) );
        }
        else
        {
            contentData = new ContentData();
            contentData.setDataSet( dataSetSerializer.parse( contentDataNode, contentType.getConfigItems() ) );
        }

        return contentData;
    }
}
