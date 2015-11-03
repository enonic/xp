package com.enonic.xp.lib.auth;

import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class GetPrincipalHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    private PrincipalKey principalKey;

    private Supplier<SecurityService> securityService;

    public void setPrincipalKey( final String principalKey )
    {
        if ( principalKey == null )
        {
            this.principalKey = null;
        }
        else
        {
            this.principalKey = PrincipalKey.from( principalKey );
        }
    }

    public PrincipalMapper getPrincipal()
    {
        final AuthenticationInfo authInfo = this.context.get().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            final Optional<? extends Principal> principal = this.securityService.get().getPrincipal( this.principalKey );
            if ( principal.isPresent() )
            {
                return new PrincipalMapper( principal.get() );
            }
            return null;
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
        this.securityService = context.getService( SecurityService.class );
    }
}
