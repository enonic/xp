package com.enonic.xp.portal.impl.auth;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public class AuthControllerWorker
{
    private final AuthControllerService authControllerService;

    private final HttpServletRequest request;

    public AuthControllerWorker( final AuthControllerService authControllerService, final HttpServletRequest request )
    {
        this.authControllerService = authControllerService;
        this.request = request;
    }

    public boolean execute( final String functionName )
        throws IOException
    {
        return serialize( functionName, null );
    }

    public boolean serialize( final String functionName, final HttpServletResponse response )
        throws IOException
    {
        final UserStoreKey userStoreKey = retrieveUserStoreKey();
        return authControllerService.serialize( userStoreKey, request, functionName, response );
    }

    private UserStoreKey retrieveUserStoreKey()
    {
        UserStoreKey userStoreKey = null;
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( request );
        if ( virtualHost != null )
        {
            userStoreKey = virtualHost.getUserStoreKey();
        }
        if ( userStoreKey == null )
        {
            userStoreKey = UserStoreKey.system();
        }
        return userStoreKey;
    }
}
