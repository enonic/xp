package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

public final class CreateUserHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private IdProviderKey idProviderKey;

    private String name;

    private String displayName;

    private String email;

    public void setIdProvider( final String idProvider )
    {
        this.idProviderKey = IdProviderKey.from( idProvider );
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
        final User user = this.securityService.get().createUser( CreateUserParams.create().
            displayName( this.displayName != null ? this.displayName : this.name ).
            email( this.email ).
            login( this.name ).
            userKey( PrincipalKey.ofUser( this.idProviderKey, this.name ) ).
            build() );
        return new PrincipalMapper( user );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
