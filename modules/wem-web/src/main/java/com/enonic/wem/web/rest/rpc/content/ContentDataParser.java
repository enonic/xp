package com.enonic.wem.web.rest.rpc.content;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.DataTypeFixer;

final class ContentDataParser
{
    private ContentType contentType;

    ContentDataParser( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    ContentDataParser()
    {
    }

    ContentData parse( final ObjectNode data )
    {
        final ContentData contentData = new ContentData();

        final Iterator<String> fieldNames = data.getFieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            final EntryPath entryPath = new EntryPath( fieldName );

            final JsonNode valueNode = data.get( fieldName );
            if ( valueNode.isObject() )
            {
                contentData.setData( entryPath, parseDataSet( entryPath, valueNode ), DataTypes.DATA_SET );
            }
            else if ( valueNode.isValueNode() )
            {
                final String fieldValue = valueNode.getTextValue();
                contentData.setData( entryPath, fieldValue );
            }
        }

        if ( contentType != null )
        {
            new DataTypeFixer( contentType ).fix( contentData );
        }

        return contentData;
    }

    private Object parseDataSet( final EntryPath entryPath, final JsonNode valueNode )
    {
        DataSet dataSet = new DataSet( entryPath );
        Iterator<String> fieldNames = valueNode.getFieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            JsonNode childNode = valueNode.get( fieldName );
            if ( childNode.isValueNode() )
            {
                final String valueAsString = childNode.getTextValue();
                final Data data =
                    Data.newData().path( new EntryPath( entryPath, fieldName ) ).type( DataTypes.TEXT ).value( valueAsString ).build();
                dataSet.add( data );
            }
        }
        return dataSet;
    }
}
