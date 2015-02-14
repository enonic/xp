package com.enonic.xp.admin.impl.json.schema.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.core.schema.content.ContentType;
import com.enonic.xp.core.schema.content.ContentTypes;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeSummaryListJson
    implements ContentTypeList
{
    private final ImmutableList<ContentTypeSummaryJson> list;

    public ContentTypeSummaryListJson( final ContentTypes contentTypes, final ContentTypeIconUrlResolver iconUrlResolver )
    {
        final ImmutableList.Builder<ContentTypeSummaryJson> builder = ImmutableList.builder();
        for ( final ContentType contentType : contentTypes )
        {
            builder.add( new ContentTypeSummaryJson( contentType, iconUrlResolver ) );
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
