package com.enonic.xp.portal.impl;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public final class TestPortalHandler
    implements PortalHandler
{
    protected PortalResponse response;

    protected RequestVerifier verifier = req -> {
    };

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
        this.verifier.verify( req );
        return this.response;
    }
}
