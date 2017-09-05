package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetMembersHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testExamples()
    {
        final Group group = TestDataFixtures.getTestGroup();
        final User user1 = TestDataFixtures.getTestUser();
        final User user2 = TestDataFixtures.getTestUser2();
        final PrincipalKeys principalKeys = PrincipalKeys.from( user1.getKey(), user2.getKey() );

        final PrincipalRelationships relationships =
            PrincipalRelationships.from( PrincipalRelationship.from( group.getKey() ).to( user1.getKey() ),
                                         PrincipalRelationship.from( group.getKey() ).to( user2.getKey() ) );
        Mockito.when( securityService.getRelationships( group.getKey() ) ).thenReturn( relationships );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( user1, user2 ) );

        runScript( "/site/lib/xp/examples/auth/getMembers.js" );
    }

    @Test
    public void testGetMembers()
    {
        final Group group = TestDataFixtures.getTestGroup();
        final User user1 = TestDataFixtures.getTestUser();
        final User user2 = TestDataFixtures.getTestUser2();
        final PrincipalKeys principalKeys = PrincipalKeys.from( user1.getKey(), user2.getKey() );

        final PrincipalRelationships relationships =
            PrincipalRelationships.from( PrincipalRelationship.from( group.getKey() ).to( user1.getKey() ),
                                         PrincipalRelationship.from( group.getKey() ).to( user2.getKey() ) );
        Mockito.when( securityService.getRelationships( group.getKey() ) ).thenReturn( relationships );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( user1, user2 ) );

        runFunction( "/site/test/getMembers-test.js", "getMembers" );
    }

    @Test
    public void testGetNoMembers()
    {
        final Group group = TestDataFixtures.getTestGroup();

        final PrincipalRelationships relationships = PrincipalRelationships.empty();
        Mockito.when( securityService.getRelationships( group.getKey() ) ).thenReturn( relationships );

        Mockito.when( securityService.getPrincipals( PrincipalKeys.empty() ) ).thenReturn( Principals.empty() );

        runFunction( "/site/test/getMembers-test.js", "getNoMembers" );
    }
}
