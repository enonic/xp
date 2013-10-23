package com.enonic.wem.portal.content;

import com.enonic.wem.portal.dispatch.PortalRequest;

public class PageRequest
    extends PortalRequest
{
    private PortalRequest portalRequest;

    public PageRequest( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
    }

    public PortalRequest getPortalRequest()
    {
        return portalRequest;
    }
}
