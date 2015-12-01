package com.enonic.xp.lib.context;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

public final class ContextHandlerBean
    implements ScriptBean
{
    private Supplier<Context> context;

    private Supplier<SecurityService> securityService;

    public Object run( final ContextRunParams params )
    {
        final ContextBuilder builder = ContextBuilder.from( this.context.get() );
        applyUser( builder, params.username, params.userStore );
        applyBranch( builder, params.branch );

        return builder.build().
            callWith( params.callback );
    }

    public ContextMapper get()
    {
        return new ContextMapper( this.context.get() );
    }

    public ContextRunParams newRunParams()
    {
        return new ContextRunParams();
    }

    private void applyUser( final ContextBuilder builder, final String username, final String userStore )
    {
        if ( username == null )
        {
            return;
        }

        final AuthenticationInfo authInfo = runAsAuthenticated( () -> getAuthenticationInfo( username, userStore ) );
        builder.authInfo( authInfo );
    }

    private void applyBranch( final ContextBuilder builder, final String branch )
    {
        if ( branch == null )
        {
            return;
        }

        builder.branch( branch );
    }

    private AuthenticationInfo getAuthenticationInfo( final String username, final String userStore )
    {
        final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken();
        token.setUsername( username );
        token.setUserStore( userStore == null ? null : UserStoreKey.from( userStore ) );
        return this.securityService.get().authenticate( token );
    }

    private <T> T runAsAuthenticated( final Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( this.context.get() ).
            authInfo( authInfo ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
        this.securityService = context.getService( SecurityService.class );
    }
}
