package com.enonic.xp.lib.auth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.mockito.Matchers.eq;

public class RemoveMembersHandlerTest
    extends ScriptTestSupport
{
    private final PrincipalKey USER = PrincipalKey.from( "user:myUserStore:userId" );

    private final PrincipalKey GROUP = PrincipalKey.from( "group:myGroupStore:groupId" );

    private final PrincipalKey GROUP2 = PrincipalKey.from( "group:myGroupStore:groupId2" );

    private final PrincipalKey ROLE = PrincipalKey.from( "role:roleId" );

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testRemoveMembersFromUser()
        throws Exception
    {
        try
        {
            runTestFunction( "/test/removeMembers-test.js", "removeMembersFromUser" );
            Assert.fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e.getCause() instanceof IllegalArgumentException );
            Assert.assertEquals( "Principal relationship from User to another Principal is not allowed", e.getMessage() );
        }
    }

    @Test
    public void testRemoveMembersFromRole()
        throws Exception
    {
        runTestFunction( "/test/removeMembers-test.js", "removeMembersFromRole" );

        Mockito.verify( this.securityService ).removeRelationship( eq( PrincipalRelationship.from( ROLE ).to( USER ) ) );
        Mockito.verify( this.securityService ).removeRelationship( eq( PrincipalRelationship.from( ROLE ).to( GROUP ) ) );
    }

    @Test
    public void testRemoveMembersFromGroup()
        throws Exception
    {
        runTestFunction( "/test/removeMembers-test.js", "removeMembersFromGroup" );

        Mockito.verify( this.securityService ).removeRelationship( eq( PrincipalRelationship.from( GROUP ).to( USER ) ) );
        Mockito.verify( this.securityService ).removeRelationship( eq( PrincipalRelationship.from( GROUP ).to( GROUP2 ) ) );
    }

    @Test
    public void testRemoveMembersEmptyListPassed()
        throws Exception
    {
        runTestFunction( "/test/removeMembers-test.js", "removeMembersEmptyList" );
        Mockito.verify( this.securityService, Mockito.times( 0 ) ).removeRelationship( Mockito.any() );
    }

}
