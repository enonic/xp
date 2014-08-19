package com.enonic.wem.admin.json.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;


@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractContentListJson<T extends ContentIdJson>
{
    protected final ContentIconUrlResolver iconUrlResolver;

    private final ContentListMetaDataJson metadata;

    private ImmutableList<T> contents;

    public AbstractContentListJson( final Content content, ContentListMetaData contentListMetaData,
                                    final ContentIconUrlResolver iconUrlResolver )
    {
        this( Contents.from( content ), contentListMetaData, iconUrlResolver );
    }

    public AbstractContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                    final ContentIconUrlResolver iconUrlResolver )
    {
        this.iconUrlResolver = iconUrlResolver;
        this.metadata = new ContentListMetaDataJson( contentListMetaData );

        final ImmutableList.Builder<T> builder = ImmutableList.builder();
        for ( final Content content : contents )
        {
            builder.add( createItem( content ) );
        }

        this.contents = builder.build();
    }

    public List<T> getContents()
    {
        return contents;
    }

    public ContentListMetaDataJson getMetadata()
    {
        return metadata;
    }

    protected abstract T createItem( Content content );

}
