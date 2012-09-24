package com.enonic.wem.core.content;


import java.util.Map;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.DataTypeFixer;

public class ContentFormParser
{
    private final ContentType contentType;

    public ContentFormParser( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public ContentData parse( final Map<String, String> submittedValues )
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
}
