package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;

public class ContentSummaryListJson
    extends AbstractContentListJson<ContentSummaryJson>
{
    public ContentSummaryListJson( final Content content, final ContentListMetaData contentListMetaData )
    {
        super( content, contentListMetaData );
    }

    public ContentSummaryListJson( final Contents contents, final ContentListMetaData contentListMetaData )
    {
        super( contents, contentListMetaData );
    }

    @Override
    protected ContentSummaryJson createItem( final Content content )
    {
        return new ContentSummaryJson( content );
    }
}
