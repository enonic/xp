package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

public class HasUserPasswordHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private PrincipalKey userKey;

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }

    public void setUserKey( final String key )
    {
        this.userKey = PrincipalKey.from( key );
    }

    public boolean execute()
    {
        final User user = securityService.get().getUser( userKey ).orElse( null );
        return user != null && user.getAuthenticationHash() != null;
    }
}
