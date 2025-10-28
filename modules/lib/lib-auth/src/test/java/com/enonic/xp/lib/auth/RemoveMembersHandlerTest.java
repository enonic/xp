package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;

class RemoveMembersHandlerTest
    extends ScriptTestSupport
{
    private static final PrincipalKey USER = PrincipalKey.from( "user:myIdProvider:userId" );

    private static final PrincipalKey GROUP = PrincipalKey.from( "group:myGroupStore:groupId" );

    private static final PrincipalKey GROUP2 = PrincipalKey.from( "group:myGroupStore:groupId2" );

    private static final PrincipalKey ROLE = PrincipalKey.from( "role:roleId" );

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
    void testExamples()
    {
        runScript( "/lib/xp/examples/auth/removeMembers.js" );
    }

    @Test
    void testRemoveMembersFromUser()
    {
        try
        {
            runFunction( "/test/removeMembers-test.js", "removeMembersFromUser" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e.getCause() instanceof IllegalArgumentException );
            assertEquals( "Principal relationship from User to another Principal is not allowed", e.getMessage() );
        }
    }

    @Test
    void testRemoveMembersFromRole()
    {
        runFunction( "/test/removeMembers-test.js", "removeMembersFromRole" );

        Mockito.verify( this.securityService ).removeRelationship( eq( PrincipalRelationship.from( ROLE ).to( USER ) ) );
        Mockito.verify( this.securityService ).removeRelationship( eq( PrincipalRelationship.from( ROLE ).to( GROUP ) ) );
    }

    @Test
    void testRemoveMembersFromGroup()
    {
        runFunction( "/test/removeMembers-test.js", "removeMembersFromGroup" );

        Mockito.verify( this.securityService ).removeRelationship( eq( PrincipalRelationship.from( GROUP ).to( USER ) ) );
        Mockito.verify( this.securityService ).removeRelationship( eq( PrincipalRelationship.from( GROUP ).to( GROUP2 ) ) );
    }

    @Test
    void testRemoveMembersEmptyListPassed()
    {
        runFunction( "/test/removeMembers-test.js", "removeMembersEmptyList" );
        Mockito.verify( this.securityService, Mockito.times( 0 ) ).removeRelationship( Mockito.any() );
    }
}
