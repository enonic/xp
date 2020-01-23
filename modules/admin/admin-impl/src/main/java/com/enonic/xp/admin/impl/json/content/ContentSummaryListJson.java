package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;

public class ContentSummaryListJson
    extends AbstractContentListJson<ContentSummaryJson>
{
    public ContentSummaryListJson( final Content content, final ContentListMetaData contentListMetaData,
                                   final ContentIconUrlResolver iconUrlResolver )
    {
        super( content, contentListMetaData, iconUrlResolver, null, null );
    }

    public ContentSummaryListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                   final ContentIconUrlResolver iconUrlResolver )
    {
        super( contents, contentListMetaData, iconUrlResolver, null, null );
    }

    @Override
    protected ContentSummaryJson createItem( final Content content )
    {
        return new ContentSummaryJson( content, iconUrlResolver );
    }
}
