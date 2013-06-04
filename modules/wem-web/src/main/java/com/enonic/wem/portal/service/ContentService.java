package com.enonic.wem.portal.service;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.portal.request.PortalRequest;

public interface ContentService
{
    public Content getContent( final PortalRequest portalRequest );
}
