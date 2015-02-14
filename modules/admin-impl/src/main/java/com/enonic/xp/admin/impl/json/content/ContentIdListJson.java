package com.enonic.xp.admin.impl.json.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;

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
