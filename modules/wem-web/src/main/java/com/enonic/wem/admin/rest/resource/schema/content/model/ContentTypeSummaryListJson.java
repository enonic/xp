package com.enonic.wem.admin.rest.resource.schema.content.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;

public class ContentTypeSummaryListJson
    implements ContentTypeList
{
    private final ImmutableList<ContentTypeSummaryJson> list;

    public ContentTypeSummaryListJson( final ContentTypes contentTypes )
    {
        final ImmutableList.Builder<ContentTypeSummaryJson> builder = ImmutableList.builder();
        for ( final ContentType contentType : contentTypes )
        {
            builder.add( new ContentTypeSummaryJson( contentType ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<ContentTypeSummaryJson> getContentTypes()
    {
        return this.list;
    }
}
