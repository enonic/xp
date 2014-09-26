package com.enonic.wem.admin.json.content;

import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;

@SuppressWarnings("UnusedDeclaration")
public class ContentListJson
    extends AbstractContentListJson<ContentJson>
{
    public ContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                            final ContentIconUrlResolver iconUrlResolver,
                            final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer )
    {
        super( content, contentListMetaData, iconUrlResolver, mixinReferencesToFormItemsTransformer );
    }

    public ContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                            final ContentIconUrlResolver iconUrlResolver,
                            final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer )
    {
        super( contents, contentListMetaData, iconUrlResolver, mixinReferencesToFormItemsTransformer );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content, iconUrlResolver, mixinReferencesToFormItemsTransformer );
    }
}
