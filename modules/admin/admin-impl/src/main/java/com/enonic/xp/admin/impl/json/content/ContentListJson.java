package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;

@SuppressWarnings("UnusedDeclaration")
public class ContentListJson
    extends AbstractContentListJson<ContentJson>
{
    public ContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                            final ContentIconUrlResolver iconUrlResolver, final ContentPrincipalsResolver contentPrincipalsResolver,
                            final ComponentNameResolver componentNameResolver )
    {
        super( content, contentListMetaData, iconUrlResolver, contentPrincipalsResolver, componentNameResolver );
    }

    public ContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                            final ContentIconUrlResolver iconUrlResolver, final ContentPrincipalsResolver contentPrincipalsResolver,
                            final ComponentNameResolver componentNameResolver )
    {
        super( contents, contentListMetaData, iconUrlResolver, contentPrincipalsResolver, componentNameResolver );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content, iconUrlResolver, contentPrincipalsResolver, componentNameResolver );
    }
}
