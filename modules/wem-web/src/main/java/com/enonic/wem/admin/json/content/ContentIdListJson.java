package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

public class ContentIdListJson
    extends AbstractContentListJson<ContentIdJson>
{
    public ContentIdListJson( final Content content )
    {
        super( content );
    }

    public ContentIdListJson( final Contents contents )
    {
        super( contents );
    }

    @Override
    protected ContentIdJson createItem( final Content content )
    {
        return new ContentIdJson( content );
    }
}
