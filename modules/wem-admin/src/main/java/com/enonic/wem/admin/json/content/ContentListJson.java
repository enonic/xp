package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;

@SuppressWarnings("UnusedDeclaration")
public class ContentListJson
    extends AbstractContentListJson<ContentJson>
{
    public ContentListJson( final Content content, final ContentListMetaData contentListMetaData )
    {
        super( content, contentListMetaData );
    }

    public ContentListJson( final Contents contents, final ContentListMetaData contentListMetaData )
    {
        super( contents, contentListMetaData );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content );
    }
}
