package com.enonic.xp.portal.auth;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface AuthControllerScript
{
    PortalResponse execute( String functionName, PortalRequest portalRequest );
}
