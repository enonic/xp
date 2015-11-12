package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;

public final class CreateUserHandler
    implements ScriptBean
{
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
        final User user = this.securityService.get().createUser(
            CreateUserParams.create().displayName( this.displayName ).email( this.email ).login( this.name ).userKey(
                PrincipalKey.ofUser( this.userStore, this.name ) ).build() );
        return user != null ? new PrincipalMapper( user ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
