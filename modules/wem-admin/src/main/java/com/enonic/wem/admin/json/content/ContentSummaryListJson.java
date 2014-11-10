package com.enonic.wem.admin.json.content;

import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;

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
