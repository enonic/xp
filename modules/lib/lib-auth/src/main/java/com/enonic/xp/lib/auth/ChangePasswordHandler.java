package com.enonic.xp.lib.auth;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ChangePasswordHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    public void changePassword( String password )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        final PrincipalKey key = authInfo.getUser().getKey();

        runAsAuthenticated( () -> this.securityService.get().setPassword( key, password ) );
    }

    private <T> T runAsAuthenticated( Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( authInfo ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }

}
