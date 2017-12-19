package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;

public class ChangePasswordHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private String userKey;

    private String password;

    public void changePassword()
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );

        this.securityService.get().setPassword( principalKey, normalize( password ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }

    public void setUserKey( final String userKey )
    {
        this.userKey = userKey;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    private String normalize( final String value )
    {
        return value.replaceAll( "\\s", "" );
    }
}
