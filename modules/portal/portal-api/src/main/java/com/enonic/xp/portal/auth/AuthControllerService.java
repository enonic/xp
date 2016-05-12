package com.enonic.xp.portal.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.security.UserStoreKey;

public interface AuthControllerService
{
    boolean execute( UserStoreKey userStoreKey, HttpServletRequest request, String functionName )
        throws IOException;

    boolean serialize( UserStoreKey userStoreKey, HttpServletRequest request, String functionName, HttpServletResponse response )
        throws IOException;
}
