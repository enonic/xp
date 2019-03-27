package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.Matchers.eq;

public class AddMembersHandlerTest
    extends ScriptTestSupport
{
    private final PrincipalKey USER = PrincipalKey.from( "user:mystore:user1" );

    private final PrincipalKey GROUP = PrincipalKey.from( "group:mystore:group1" );

    private final PrincipalKey ROLE = PrincipalKey.from( "role:roleId" );

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
        runScript( "/lib/xp/examples/auth/addMembers.js" );

        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( USER ) ) );
        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( GROUP ) ) );
    }

    @Test
    public void testAddUserAndGroupMembersToRole()
    {
        runFunction( "/test/addMembers-test.js", "addUserAndGroupMembersToRole" );

        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( USER ) ) );
        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( GROUP ) ) );
    }

    @Test
    public void testAddGroupMemberToRole()
    {
        runFunction( "/test/addMembers-test.js", "addGroupMemberToRole" );
        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( GROUP ) ) );
    }

    @Test
    public void testAddMembersEmptyListPassed()
    {
        runFunction( "/test/addMembers-test.js", "addMembersEmptyList" );
        Mockito.verify( this.securityService, Mockito.times( 0 ) ).addRelationship( Mockito.any() );
    }
}
