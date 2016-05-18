package com.enonic.xp.portal.impl.auth;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.security.UserStoreKey;

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
        return execute( functionName, null );
    }

    public boolean execute( final String functionName, final HttpServletResponse response )
        throws IOException
    {
        final UserStoreKey userStoreKey = authControllerService.retrieveUserStoreKey( request );
        final AuthControllerExecutionParams executionParams = AuthControllerExecutionParams.create().
            userStoreKey( userStoreKey ).
            functionName( functionName ).
            request( request ).
            response( response ).
            build();
        return authControllerService.execute( executionParams ) != null;
    }
}
