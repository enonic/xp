package com.enonic.xp.admin.impl.json.schema.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypes;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeListJson
    implements ContentTypeList
{
    private final ImmutableList<ContentTypeJson> list;

    public ContentTypeListJson( final ContentTypes contentTypes, final ContentTypeIconUrlResolver iconUrlResolver,
                                final LocaleMessageResolver localeMessageResolver )
    {
        final ImmutableList.Builder<ContentTypeJson> builder = ImmutableList.builder();
        for ( final ContentType contentType : contentTypes )
        {
            builder.add( new ContentTypeJson( contentType, iconUrlResolver, localeMessageResolver ) );
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
