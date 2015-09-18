package com.enonic.xp.lib.auth;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.session.Session;

public final class LogoutHandler
{

    public void logout()
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session != null )
        {
            session.invalidate();
        }
    }

}
