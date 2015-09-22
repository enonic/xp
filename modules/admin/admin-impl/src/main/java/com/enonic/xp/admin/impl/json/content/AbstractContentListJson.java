package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;


@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractContentListJson<T extends ContentIdJson>
{
    protected final ContentIconUrlResolver iconUrlResolver;

    private final ContentListMetaDataJson metadata;

    protected final ContentPrincipalsResolver contentPrincipalsResolver;

    private ImmutableList<T> contents;

    public AbstractContentListJson( final Content content, ContentListMetaData contentListMetaData,
                                    final ContentIconUrlResolver iconUrlResolver,
                                    final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        this( Contents.from( content ), contentListMetaData, iconUrlResolver, contentPrincipalsResolver );
    }

    public AbstractContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                    final ContentIconUrlResolver iconUrlResolver,
                                    final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        this.iconUrlResolver = iconUrlResolver;
        this.contentPrincipalsResolver = contentPrincipalsResolver;
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
