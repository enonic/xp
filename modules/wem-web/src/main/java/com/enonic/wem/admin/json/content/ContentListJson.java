package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

public class ContentListJson
    extends AbstractContentListJson<ContentJson>
{
    public ContentListJson( final Content content )
    {
        super( content );
    }

    public ContentListJson( final Contents contents )
    {
        super( contents );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content );
    }
}
