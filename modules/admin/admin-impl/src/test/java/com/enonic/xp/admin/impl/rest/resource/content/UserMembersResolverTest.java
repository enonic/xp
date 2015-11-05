package com.enonic.xp.admin.impl.rest.resource.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;


public class UserMembersResolverTest
{

    private static final UserStoreKey USER_STORE = UserStoreKey.from( "myUserStore" );

    private static final PrincipalKey GROUP_1 = PrincipalKey.ofGroup( USER_STORE, "group_1" );

    private static final PrincipalKey GROUP_2 = PrincipalKey.ofGroup( USER_STORE, "group_2" );

    private static final PrincipalKey USER_1 = PrincipalKey.ofUser( USER_STORE, "user_1" );

    private static final PrincipalKey USER_2 = PrincipalKey.ofUser( USER_STORE, "user_2" );

    private static final PrincipalKey USER_3 = PrincipalKey.ofUser( USER_STORE, "user_3" );


    private SecurityService securityService;

    @Before
    public final void setUp()
        throws Exception
    {
        this.securityService = Mockito.mock( SecurityService.class );
    }

    @Test
    public void testGetUserMembersNone()
        throws Exception
    {
        Mockito.when( this.securityService.getRelationships( any( PrincipalKey.class ) ) ).thenReturn( PrincipalRelationships.empty() );

        final UserMembersResolver resolver = new UserMembersResolver( this.securityService );

        final PrincipalKeys res = resolver.getUserMembers( GROUP_1 );
        assertEquals( 0, res.getSize() );
    }

    @Test
    public void testGetUserMembersDirect()
        throws Exception
    {
        final PrincipalRelationships memberships = PrincipalRelationships.from( PrincipalRelationship.from( GROUP_1 ).to( USER_1 ),
                                                                                PrincipalRelationship.from( GROUP_1 ).to( USER_2 ),
                                                                                PrincipalRelationship.from( GROUP_1 ).to( USER_3 ) );
        Mockito.when( this.securityService.getRelationships( eq( GROUP_1 ) ) ).
            thenReturn( memberships );

        final UserMembersResolver resolver = new UserMembersResolver( this.securityService );

        final PrincipalKeys res = resolver.getUserMembers( GROUP_1 );
        assertEquals( 3, res.getSize() );
    }

    @Test
    public void testGetUserMembersIndirect()
        throws Exception
    {
        final PrincipalRelationships group1Memberships = PrincipalRelationships.from( PrincipalRelationship.from( GROUP_1 ).to( GROUP_2 ) );
        final PrincipalRelationships group2Memberships = PrincipalRelationships.from( PrincipalRelationship.from( GROUP_2 ).to( USER_1 ),
                                                                                      PrincipalRelationship.from( GROUP_2 ).to( USER_2 ),
                                                                                      PrincipalRelationship.from( GROUP_2 ).to( USER_3 ) );
        Mockito.when( this.securityService.getRelationships( eq( GROUP_1 ) ) ).
            thenReturn( group1Memberships );
        Mockito.when( this.securityService.getRelationships( eq( GROUP_2 ) ) ).
            thenReturn( group2Memberships );

        final UserMembersResolver resolver = new UserMembersResolver( this.securityService );

        final PrincipalKeys res = resolver.getUserMembers( GROUP_1 );
        assertEquals( 3, res.getSize() );
    }

    @Test
    public void testGetUserMembersDuplicates()
        throws Exception
    {
        final PrincipalRelationships group1Memberships = PrincipalRelationships.from( PrincipalRelationship.from( GROUP_1 ).to( GROUP_2 ),
                                                                                      PrincipalRelationship.from( GROUP_1 ).to( USER_1 ),
                                                                                      PrincipalRelationship.from( GROUP_1 ).to( USER_3 ) );
        final PrincipalRelationships group2Memberships = PrincipalRelationships.from( PrincipalRelationship.from( GROUP_2 ).to( USER_1 ),
                                                                                      PrincipalRelationship.from( GROUP_2 ).to( USER_2 ),
                                                                                      PrincipalRelationship.from( GROUP_2 ).to( USER_3 ) );
        Mockito.when( this.securityService.getRelationships( eq( GROUP_1 ) ) ).
            thenReturn( group1Memberships );
        Mockito.when( this.securityService.getRelationships( eq( GROUP_2 ) ) ).
            thenReturn( group2Memberships );

        final UserMembersResolver resolver = new UserMembersResolver( this.securityService );

        final PrincipalKeys res = resolver.getUserMembers( GROUP_1 );
        assertEquals( 3, res.getSize() );
    }
}