package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class CreateUserHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    private Supplier<SecurityService> securityService;

    private UserStoreKey userStore;

    private String name;

    private String displayName;

    private String email;

    public void setUserStore( final String userStore )
    {
        this.userStore = UserStoreKey.from( userStore );
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }

    public PrincipalMapper createUser()
    {
        final AuthenticationInfo authInfo = this.context.get().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return new PrincipalMapper( this.securityService.get().createUser(
                CreateUserParams.create().displayName( this.displayName ).email( this.email ).login( this.name ).userKey(
                    PrincipalKey.ofUser( this.userStore, this.name ) ).build() ) );
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
