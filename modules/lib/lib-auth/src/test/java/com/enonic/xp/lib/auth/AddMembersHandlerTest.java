package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.ArgumentMatchers.eq;

class AddMembersHandlerTest
    extends ScriptTestSupport
{
    private static final PrincipalKey USER = PrincipalKey.from( "user:mystore:user1" );

    private static final PrincipalKey GROUP = PrincipalKey.from( "group:mystore:group1" );

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
        runScript( "/lib/xp/examples/auth/addMembers.js" );

        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( USER ) ) );
        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( GROUP ) ) );
    }

    @Test
    void testAddUserAndGroupMembersToRole()
    {
        runFunction( "/test/addMembers-test.js", "addUserAndGroupMembersToRole" );

        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( USER ) ) );
        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( GROUP ) ) );
    }

    @Test
    void testAddGroupMemberToRole()
    {
        runFunction( "/test/addMembers-test.js", "addGroupMemberToRole" );
        Mockito.verify( this.securityService ).addRelationship( eq( PrincipalRelationship.from( ROLE ).to( GROUP ) ) );
    }

    @Test
    void testAddMembersEmptyListPassed()
    {
        runFunction( "/test/addMembers-test.js", "addMembersEmptyList" );
        Mockito.verify( this.securityService, Mockito.times( 0 ) ).addRelationship( Mockito.any() );
    }

    @Test
    void testAddMembersWithoutKey()
    {
        runFunction( "/test/addMembers-test.js", "addMembersWithoutKey" );
    }
}
