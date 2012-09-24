package com.enonic.wem.core.content.data;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;

public class ContentDataSerializerJson
{
    private DataSerializerJson dataSerializer = new DataSerializerJson();

    public void generate( ContentData contentData, JsonGenerator g )
        throws IOException
    {
        g.writeArrayFieldStart( "data" );
        for ( final Data data : contentData )
        {
            dataSerializer.generate( data, g );
        }
        g.writeEndArray();
    }

    public ContentData parse( final JsonNode contentNode )
    {
        final JsonNode contentDataNode = contentNode.get( "data" );

        ContentData contentData = new ContentData();
        DataSet dataSet = new DataSet( new EntryPath( "" ) );
        final Iterator<JsonNode> dataIt = contentDataNode.getElements();
        while ( dataIt.hasNext() )
        {
            final JsonNode eNode = dataIt.next();
            dataSet.add( dataSerializer.parse( new EntryPath(), eNode ) );
        }
        contentData.setDataSet( dataSet );

        return contentData;
    }
}
