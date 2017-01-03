package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class GetUserHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    private boolean includeProfile;

    public void setIncludeProfile( final boolean includeProfile )
    {
        this.includeProfile = includeProfile;
    }

    public PrincipalMapper getUser()
    {
        final AuthenticationInfo authInfo = this.context.get().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return new PrincipalMapper( authInfo.getUser(), this.includeProfile );
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
