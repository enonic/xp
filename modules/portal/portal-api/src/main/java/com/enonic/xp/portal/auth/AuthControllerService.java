package com.enonic.xp.portal.auth;

import java.io.IOException;

import com.enonic.xp.portal.PortalResponse;

public interface AuthControllerService
{
    PortalResponse execute( AuthControllerExecutionParams params )
        throws IOException;
}
