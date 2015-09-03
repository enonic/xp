package com.enonic.xp.lib.auth;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class GetUserHandler
{

    public UserMapper getUser()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return new UserMapper( authInfo.getUser() );
        }
        else
        {
            return null;
        }
    }

}
