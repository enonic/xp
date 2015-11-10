package com.enonic.xp.lib.auth;

import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;

public final class GetPrincipalHandler
    implements ScriptBean
{
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
        final Optional<? extends Principal> principal = this.securityService.get().getPrincipal( this.principalKey );
        if ( principal.isPresent() )
        {
            return new PrincipalMapper( principal.get() );
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
