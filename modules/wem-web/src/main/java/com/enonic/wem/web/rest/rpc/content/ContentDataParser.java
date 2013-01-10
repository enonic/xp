package com.enonic.wem.web.rest.rpc.content;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.EntryPath;
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
            final EntryPath path = EntryPath.from( fieldName );

            final JsonNode valueNode = data.get( fieldName );

            if ( valueNode.isValueNode() )
            {
                final String fieldValue = valueNode.getTextValue();
                contentData.setData( path, fieldValue );
            }
            /*else if ( valueNode.isObject() )
            {
                final DataSet dataSet = parseDataSet( path, valueNode, contentData.getDataSet() );
                contentData.add( dataSet );
            }*/
        }

        if ( contentType != null )
        {
            new DataTypeFixer( contentType ).fix( contentData );
        }

        return contentData;
    }

    /*private DataSet parseDataSet( final EntryPath path, final JsonNode valueNode, final DataSet parent )
    {
        DataSet dataSet = new DataSet( path.getLastElement().getName(), parent );
        Iterator<String> fieldNames = valueNode.getFieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            JsonNode childNode = valueNode.get( fieldName );
            if ( childNode.isValueNode() )
            {
                final String valueAsString = childNode.getTextValue();
                final Data data = Data.newData().name( fieldName ).type( DataTypes.TEXT ).value( valueAsString ).parent( parent ).build();
                dataSet.add( data );
            }
        }
        return dataSet;
    }*/
}
