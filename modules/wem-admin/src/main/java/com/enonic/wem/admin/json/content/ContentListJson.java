package com.enonic.wem.admin.json.content;

import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;

@SuppressWarnings("UnusedDeclaration")
public class ContentListJson
    extends AbstractContentListJson<ContentJson>
{
    public ContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                            final ContentIconUrlResolver iconUrlResolver )
    {
        super( content, contentListMetaData, iconUrlResolver );
    }

    public ContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                            final ContentIconUrlResolver iconUrlResolver )
    {
        super( contents, contentListMetaData, iconUrlResolver );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content, iconUrlResolver );
    }
}
