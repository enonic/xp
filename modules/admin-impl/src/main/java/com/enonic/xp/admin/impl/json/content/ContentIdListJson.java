package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentListMetaData;
import com.enonic.xp.core.content.Contents;

public class ContentIdListJson
    extends AbstractContentListJson<ContentIdJson>
{
    public ContentIdListJson( final Content content, final ContentListMetaData contentListMetaData )
    {
        super( content, contentListMetaData, null, null, null );
    }

    public ContentIdListJson( final Contents contents, final ContentListMetaData contentListMetaData )
    {
        super( contents, contentListMetaData, null, null, null );
    }

    @Override
    protected ContentIdJson createItem( final Content content )
    {
        return new ContentIdJson( content.getId() );
    }
}
