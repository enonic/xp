package com.enonic.wem.core.content.data;


import java.util.Map;

import com.enonic.wem.core.content.type.ContentType;

public class ContentFormParser
{
    private final ContentType contentType;

    public ContentFormParser( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public ContentData parse( final Map<String, String> submittedValues )
    {
        final ContentData contentData = new ContentData( contentType );

        for ( Map.Entry<String, String> entry : submittedValues.entrySet() )
        {
            EntryPath entryPath = new EntryPath( entry.getKey() );
            String valueAsString = entry.getValue();

            contentData.setData( entryPath, valueAsString );
        }

        return contentData;
    }
}
