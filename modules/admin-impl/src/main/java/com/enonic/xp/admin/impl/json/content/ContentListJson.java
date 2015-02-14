package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentListMetaData;
import com.enonic.xp.core.content.Contents;
import com.enonic.xp.core.form.InlineMixinsToFormItemsTransformer;

@SuppressWarnings("UnusedDeclaration")
public class ContentListJson
    extends AbstractContentListJson<ContentJson>
{
    public ContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                            final ContentIconUrlResolver iconUrlResolver,
                            final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                            final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( content, contentListMetaData, iconUrlResolver, inlineMixinsToFormItemsTransformer, contentPrincipalsResolver );
    }

    public ContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                            final ContentIconUrlResolver iconUrlResolver,
                            final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                            final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( contents, contentListMetaData, iconUrlResolver, inlineMixinsToFormItemsTransformer, contentPrincipalsResolver );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content, iconUrlResolver, inlineMixinsToFormItemsTransformer, contentPrincipalsResolver );
    }
}
