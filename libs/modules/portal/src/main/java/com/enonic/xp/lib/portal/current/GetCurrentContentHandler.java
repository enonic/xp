package com.enonic.xp.lib.portal.current;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.mapper.ContentMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;

public final class GetCurrentContentHandler
{

    public ContentMapper execute()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();
        final Content content = portalRequest.getContent();
        return content != null ? convert( content ) : null;
    }

    private ContentMapper convert( final Content content )
    {
        return content == null ? null : new ContentMapper( content );
    }

}
