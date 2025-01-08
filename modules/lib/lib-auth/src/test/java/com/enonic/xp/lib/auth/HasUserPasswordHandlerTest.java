package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class HasUserPasswordHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testExamples()
    {
        final PrincipalKey userKey = PrincipalKey.ofUser( IdProviderKey.from( "enonic" ), "user1" );

        when( this.securityService.getUser( userKey ) ).thenReturn( Optional.of( TestDataFixtures.getTestUser() ) );

        runScript( "/lib/xp/examples/auth/hasUserPassword.js" );
        verify( this.securityService, times( 1 ) ).getUser( eq( userKey ) );
        verifyNoMoreInteractions( this.securityService );
    }

    @Test
    public void testHasUserPassword()
    {
        final User user = User.create( TestDataFixtures.getTestUser() ).authenticationHash( "pwd" ).build();

        final PrincipalKey userKey = PrincipalKey.from( "user:enonic:user1" );

        when( this.securityService.getUser( userKey ) ).thenReturn( Optional.of( user ) );

        runFunction( "/test/hasUserPassword-test.js", "hasUserPassword" );

        verify( this.securityService, times( 1 ) ).getUser( eq( userKey ) );
        verifyNoMoreInteractions( this.securityService );
    }
}
