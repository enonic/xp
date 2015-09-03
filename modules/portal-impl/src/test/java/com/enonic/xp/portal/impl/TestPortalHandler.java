package com.enonic.xp.portal.impl;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public final class TestPortalHandler
    implements PortalHandler
{
    protected PortalRequest request;

    protected PortalResponse response;

    @Override
    public int getOrder()
    {
        return 0;
    }

    @Override
    public boolean canHandle( final PortalRequest req )
    {
        return true;
    }

    @Override
    public PortalResponse handle( final PortalRequest req )
        throws Exception
    {
        this.request = req;
        return this.response;
    }
}
