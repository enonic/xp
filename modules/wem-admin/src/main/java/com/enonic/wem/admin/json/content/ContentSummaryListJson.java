package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

public class ContentSummaryListJson
    extends AbstractContentListJson<ContentSummaryJson>
{
    public ContentSummaryListJson( final Content content )
    {
        super( content );
    }

    public ContentSummaryListJson( final Contents contents )
    {
        super( contents );
    }

    @Override
    protected ContentSummaryJson createItem( final Content content )
    {
        return new ContentSummaryJson( content );
    }
}
