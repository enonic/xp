package com.enonic.wem.portal.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.portal.dispatch.PortalRequest;

public interface ContentService
{
    public Content getContent( final PortalRequest portalRequest );
}
