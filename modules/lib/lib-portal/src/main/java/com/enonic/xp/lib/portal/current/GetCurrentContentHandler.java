package com.enonic.xp.lib.portal.current;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.content.mapper.ContentMapper;

public final class GetCurrentContentHandler
    extends GetCurrentAbstractHandler
{
    public ContentMapper execute()
    {
        final Content content = getContent();
        return content != null ? convert( content ) : null;
    }

    private ContentMapper convert( final Content content )
    {
        return content == null ? null : new ContentMapper( content );
    }
}
