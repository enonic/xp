package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class GetUserHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    public UserMapper getUser()
    {
        final AuthenticationInfo authInfo = this.context.get().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return new UserMapper( authInfo.getUser() );
        }
        else
        {
            return null;
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
    }
}
