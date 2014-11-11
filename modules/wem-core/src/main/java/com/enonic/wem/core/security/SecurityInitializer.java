package com.enonic.wem.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;

public final class SecurityInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( SecurityInitializer.class );

    private SecurityService securityService;

    public final void init()
    {
        LOG.info( "Initializing security principals" );

        final User anonymous = User.anonymous();
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( anonymous.getKey() ).
            displayName( anonymous.getDisplayName() ).
            login( anonymous.getLogin() ).
            email( anonymous.getEmail() ).
            build();
        addUser( createUser );

        final CreateUserParams createAdmin = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( UserStoreKey.system(), "admin" ) ).
            displayName( "Administrator" ).
            login( "admin" ).
            password( "password" ).
            build();
        addUser( createAdmin );
    }

    private void addUser( final CreateUserParams createUser )
    {
        try
        {
            if ( !securityService.getUser( createUser.getKey() ).isPresent() )
            {
                securityService.createUser( createUser );
                LOG.info( "User created: " + createUser.getKey().toString() );
            }
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to initialize user: " + createUser.getKey().toString(), t );
        }
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
