package com.enonic.wem.admin.json.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

public class ContentSummaryListJson
{
    private List<ContentSummaryJson> contents;

    public ContentSummaryListJson( Content content )
    {
        this( Contents.from( content ) );
    }

    public ContentSummaryListJson( Contents contents )
    {
        final ImmutableList.Builder<ContentSummaryJson> builder = ImmutableList.builder();
        for ( final Content content : contents )
        {
            builder.add( new ContentSummaryJson( content ) );
        }

        this.contents = builder.build();
    }

    public List<ContentSummaryJson> getContents()
    {
        return contents;
    }
}
