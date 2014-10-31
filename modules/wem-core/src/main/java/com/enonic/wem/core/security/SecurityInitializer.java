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

        try
        {
            if ( !securityService.getUser( PrincipalKey.ofAnonymous() ).isPresent() )
            {
                final User anonymous = User.anonymous();
                final CreateUserParams createUser = CreateUserParams.create().
                    userKey( anonymous.getKey() ).
                    displayName( anonymous.getDisplayName() ).
                    login( anonymous.getLogin() ).
                    email( anonymous.getEmail() ).
                    build();
                securityService.createUser( createUser );
                LOG.info( "Anonymous user created: " + anonymous.getKey().toString() );
            }

            final PrincipalKey adminKey = PrincipalKey.ofUser( UserStoreKey.system(), "admin" );
            if ( !securityService.getUser( adminKey ).isPresent() )
            {
                final CreateUserParams createAdmin = CreateUserParams.create().
                    userKey( adminKey ).
                    displayName( "Administrator" ).
                    login( "admin" ).
                    password( "password" ).
                    build();
                securityService.createUser( createAdmin );
                LOG.info( "Admin user created: " + adminKey.toString() );
            }
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to initialize security principals", t );
        }
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
