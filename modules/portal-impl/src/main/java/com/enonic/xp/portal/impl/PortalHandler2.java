package com.enonic.xp.portal.impl;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface PortalHandler2
{
    int getOrder();

    boolean canHandle( PortalRequest req );

    PortalResponse handle( PortalRequest req )
        throws Exception;
}
