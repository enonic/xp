package com.enonic.xp.portal.handler;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface PortalHandler
{
    int getOrder();

    boolean canHandle( PortalRequest req );

    PortalResponse handle( PortalRequest req )
        throws Exception;
}
