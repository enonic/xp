package com.enonic.wem.web.rest.rpc.content;


import java.util.Iterator;
import java.util.Map;

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

    ContentData parse( final Map<String, String> submittedValues )
    {
        final ContentData contentData = new ContentData();

        for ( Map.Entry<String, String> entry : submittedValues.entrySet() )
        {
            EntryPath entryPath = new EntryPath( entry.getKey() );
            String valueAsString = entry.getValue();

            contentData.setData( entryPath, valueAsString );
        }

        new DataTypeFixer( contentType ).fix( contentData );

        return contentData;
    }

    ContentData parse( final ObjectNode data )
    {

        final ContentData contentData = new ContentData();
        final Iterator<String> fieldNames = data.getFieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            final String fieldValue = data.get( fieldName ).getTextValue();
            EntryPath entryPath = new EntryPath( fieldName );

            contentData.setData( entryPath, fieldValue );
        }

        if ( contentType != null )
        {
            new DataTypeFixer( contentType ).fix( contentData );
        }

        return contentData;
    }
}
