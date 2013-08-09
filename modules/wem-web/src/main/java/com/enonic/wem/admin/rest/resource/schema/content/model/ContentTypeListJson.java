package com.enonic.wem.admin.rest.resource.schema.content.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;

public class ContentTypeListJson
    implements ContentTypeList
{
    private final ImmutableList<ContentTypeJson> list;

    public ContentTypeListJson( final ContentTypes contentTypes )
    {
        final ImmutableList.Builder<ContentTypeJson> builder = ImmutableList.builder();
        for ( final ContentType contentType : contentTypes )
        {
            builder.add( new ContentTypeJson( contentType ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<ContentTypeJson> getContentTypes()
    {
        return this.list;
    }
}
