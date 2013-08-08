package com.enonic.wem.admin.rest.resource.schema.content.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;

public class ContentTypeConfigListJson
    extends AbstractContentTypeJson
{
    private final ImmutableList<ContentTypeConfigJson> list;

    public ContentTypeConfigListJson( final ContentTypes contentTypes )
    {
        final ImmutableList.Builder<ContentTypeConfigJson> builder = ImmutableList.builder();
        for ( final ContentType contentType : contentTypes )
        {
            builder.add( new ContentTypeConfigJson( contentType ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<ContentTypeConfigJson> getContentTypeXmls()
    {
        return this.list;
    }
}
