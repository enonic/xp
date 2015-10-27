package com.enonic.xp.portal.impl.error;

import com.enonic.xp.portal.PortalResponse;

public interface ErrorHandlerScript
{
    PortalResponse execute( PortalError portalError );
}
