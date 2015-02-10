package com.enonic.wem.admin.json.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.admin.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.form.InlineMixinsToFormItemsTransformer;


@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractContentListJson<T extends ContentIdJson>
{
    protected final ContentIconUrlResolver iconUrlResolver;

    private final ContentListMetaDataJson metadata;

    protected final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer;

    protected final ContentPrincipalsResolver contentPrincipalsResolver;

    private ImmutableList<T> contents;

    public AbstractContentListJson( final Content content, ContentListMetaData contentListMetaData,
                                    final ContentIconUrlResolver iconUrlResolver,
                                    final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                                    final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        this( Contents.from( content ), contentListMetaData, iconUrlResolver, inlineMixinsToFormItemsTransformer,
              contentPrincipalsResolver );
    }

    public AbstractContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                    final ContentIconUrlResolver iconUrlResolver,
                                    final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                                    final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        this.iconUrlResolver = iconUrlResolver;
        this.inlineMixinsToFormItemsTransformer = inlineMixinsToFormItemsTransformer;
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
