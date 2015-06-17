package com.enonic.xp.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrincipalRelationshipTest
{
    private final static PrincipalKey USER = PrincipalKey.ofUser( UserStoreKey.system(), "user" );

    private final static PrincipalKey USER_2 = PrincipalKey.ofUser( UserStoreKey.system(), "user2" );

    private final static PrincipalKey GROUP = PrincipalKey.ofGroup( UserStoreKey.system(), "group" );

    private final static PrincipalKey GROUP_2 = PrincipalKey.ofGroup( UserStoreKey.system(), "group2" );

    private final static PrincipalKey ROLE = PrincipalKey.ofRole( "role" );

    private final static PrincipalKey ROLE_2 = PrincipalKey.ofRole( "role2" );

    @Test(expected = IllegalArgumentException.class)
    public void testFromToSamePrincipal()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( GROUP ).to( GROUP );
    }

    // from Role
    @Test
    public void testFromRoleToUser()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( ROLE ).to( USER );
        assertEquals( ROLE, rel.getFrom() );
        assertEquals( USER, rel.getTo() );
        assertEquals( "role:role -> user:system:user", rel.toString() );
    }

    @Test
    public void testFromRoleToGroup()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( ROLE ).to( GROUP );
        assertEquals( ROLE, rel.getFrom() );
        assertEquals( GROUP, rel.getTo() );
        assertEquals( "role:role -> group:system:group", rel.toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromRoleToRole()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( ROLE ).to( ROLE_2 );
    }

    // from Group
    @Test
    public void testFromGroupToUser()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( GROUP ).to( USER );
        assertEquals( GROUP, rel.getFrom() );
        assertEquals( USER, rel.getTo() );
        assertEquals( "group:system:group -> user:system:user", rel.toString() );
    }

    @Test
    public void testFromGroupToGroup()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( GROUP ).to( GROUP_2 );
        assertEquals( GROUP, rel.getFrom() );
        assertEquals( GROUP_2, rel.getTo() );
        assertEquals( "group:system:group -> group:system:group2", rel.toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromGroupToRole()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( GROUP ).to( ROLE );
    }

    // from User
    @Test(expected = IllegalArgumentException.class)
    public void testFromUserToUser()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( USER ).to( USER_2 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromUserToGroup()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( USER ).to( GROUP );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromUserToRole()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( USER ).to( ROLE );
    }

    @Test
    public void testEquals()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.create( PrincipalKey.ofGroup( UserStoreKey.system(), "group" ) ).
            to( PrincipalKey.ofUser( UserStoreKey.system(), "user" ) );
        PrincipalRelationship rel2 = PrincipalRelationship.create( PrincipalKey.ofGroup( UserStoreKey.system(), "group" ) ).
            to( PrincipalKey.ofUser( UserStoreKey.system(), "user" ) );

        assertTrue( rel != rel2 );
        assertTrue( rel.getTo() != rel2.getTo() );
        assertTrue( rel.getFrom() != rel2.getFrom() );
        assertEquals( rel, rel2 );
        assertEquals( rel.hashCode(), rel2.hashCode() );
    }

}