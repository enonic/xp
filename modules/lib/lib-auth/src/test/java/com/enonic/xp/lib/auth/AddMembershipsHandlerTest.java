package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class AddMembershipsHandlerTest
    extends ScriptTestSupport
{

    private SimpleSession session;

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );

        this.session.setAttribute( HandlerTestHelper.createAuthenticationInfo() );
    }

    @Test
    public void testAddGroupAndRoleMembershipsToUser()
        throws Exception
    {

        Mockito.doAnswer(
            invocationOnMock -> invokeAddRelationshipsToUser( (PrincipalRelationship) invocationOnMock.getArguments()[0] ) ).when(
            this.securityService ).addRelationship( Mockito.any() );

        runTestFunction( "/test/addMemberships-test.js", "addGroupAndRoleMembershipsToUser" );
    }

    @Test
    public void testAddRoleMembershipsToGroup()
        throws Exception
    {

        Mockito.doAnswer(
            invocationOnMock -> invokeAddRelationshipsToGroup( (PrincipalRelationship) invocationOnMock.getArguments()[0] ) ).when(
            this.securityService ).addRelationship( Mockito.any() );

        runTestFunction( "/test/addMemberships-test.js", "addRoleMembershipsToGroup" );
    }

    @Test
    public void testAddMembershipsEmptyListPassed()
        throws Exception
    {

        Mockito.verify( this.securityService, Mockito.times( 0 ) );

        runTestFunction( "/test/addMemberships-test.js", "addMembershipsEmptyList" );
    }

    private PrincipalRelationship invokeAddRelationshipsToUser( final PrincipalRelationship relationship )
    {
        assertEquals( relationship.getTo(), PrincipalKey.from( "user:myUserStore:userId" ) );
        assertTrue( relationship.getFrom().equals( PrincipalKey.from( "role:roleId" ) ) ||
                        relationship.getFrom().equals( PrincipalKey.from( "group:myGroupStore:groupId" ) ) );
        return relationship;
    }

    private PrincipalRelationship invokeAddRelationshipsToGroup( final PrincipalRelationship relationship )
    {
        assertEquals( relationship.getTo(), PrincipalKey.from( "group:myGroupStore:groupId" ) );
        assertTrue( relationship.getFrom().equals( PrincipalKey.from( "role:roleId-1" ) ) ||
                        relationship.getFrom().equals( PrincipalKey.from( "role:roleId-2" ) ) );
        return relationship;
    }
}
