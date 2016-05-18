package com.enonic.xp.portal.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.security.UserStoreKey;

public interface AuthControllerService
{


    UserStoreKey retrieveUserStoreKey( HttpServletRequest request );

    PortalResponse execute( AuthControllerExecutionParams params )
        throws IOException;
}
