package com.enonic.xp.admin.impl.json.schema.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypes;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeSummaryListJson
    implements ContentTypeList
{
    private final ImmutableList<ContentTypeSummaryJson> list;

    public ContentTypeSummaryListJson( final ContentTypes contentTypes, final ContentTypeIconUrlResolver iconUrlResolver,
                                       final LocaleMessageResolver localeMessageResolver )
    {
        final ImmutableList.Builder<ContentTypeSummaryJson> builder = ImmutableList.builder();
        if(contentTypes != null)
        {
            for ( final ContentType contentType : contentTypes )
            {
                builder.add( new ContentTypeSummaryJson( contentType, iconUrlResolver, localeMessageResolver ) );
            }
        }

        this.list = builder.build();
    }

    public ContentTypeSummaryListJson( final ImmutableList<ContentTypeSummaryJson> list )
    {
        this.list = list;
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
