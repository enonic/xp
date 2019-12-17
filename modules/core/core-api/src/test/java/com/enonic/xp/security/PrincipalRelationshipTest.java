package com.enonic.xp.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrincipalRelationshipTest
{
    private final static PrincipalKey USER = PrincipalKey.ofUser( IdProviderKey.system(), "user" );

    private final static PrincipalKey USER_2 = PrincipalKey.ofUser( IdProviderKey.system(), "user2" );

    private final static PrincipalKey GROUP = PrincipalKey.ofGroup( IdProviderKey.system(), "group" );

    private final static PrincipalKey GROUP_2 = PrincipalKey.ofGroup( IdProviderKey.system(), "group2" );

    private final static PrincipalKey ROLE = PrincipalKey.ofRole( "role" );

    private final static PrincipalKey ROLE_2 = PrincipalKey.ofRole( "role2" );

    @Test
    public void testFromToSamePrincipal()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( GROUP ).to( GROUP )) ;
    }

    // from Role
    @Test
    public void testFromRoleToUser()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.from( ROLE ).to( USER );
        assertEquals( ROLE, rel.getFrom() );
        assertEquals( USER, rel.getTo() );
        assertEquals( "role:role -> user:system:user", rel.toString() );
    }

    @Test
    public void testFromRoleToGroup()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.from( ROLE ).to( GROUP );
        assertEquals( ROLE, rel.getFrom() );
        assertEquals( GROUP, rel.getTo() );
        assertEquals( "role:role -> group:system:group", rel.toString() );
    }

    @Test
    public void testFromRoleToRole()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( ROLE ).to( ROLE_2 ));
    }

    // from Group
    @Test
    public void testFromGroupToUser()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.from( GROUP ).to( USER );
        assertEquals( GROUP, rel.getFrom() );
        assertEquals( USER, rel.getTo() );
        assertEquals( "group:system:group -> user:system:user", rel.toString() );
    }

    @Test
    public void testFromGroupToGroup()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.from( GROUP ).to( GROUP_2 );
        assertEquals( GROUP, rel.getFrom() );
        assertEquals( GROUP_2, rel.getTo() );
        assertEquals( "group:system:group -> group:system:group2", rel.toString() );
    }

    @Test
    public void testFromGroupToRole()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalRelationship.from( GROUP ).to( ROLE ));
    }

    // from User
    @Test
    public void testFromUserToUser()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( USER ).to( USER_2 ));
    }

    @Test
    public void testFromUserToGroup()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( USER ).to( GROUP ) );
    }

    @Test
    public void testFromUserToRole()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( USER ).to( ROLE ));
    }

    @Test
    public void testEquals()
        throws Exception
    {
        PrincipalRelationship rel = PrincipalRelationship.from( PrincipalKey.ofGroup( IdProviderKey.system(), "group" ) ).
            to( PrincipalKey.ofUser( IdProviderKey.system(), "user" ) );
        PrincipalRelationship rel2 = PrincipalRelationship.from( PrincipalKey.ofGroup( IdProviderKey.system(), "group" ) ).
            to( PrincipalKey.ofUser( IdProviderKey.system(), "user" ) );

        assertTrue( rel != rel2 );
        assertTrue( rel.getTo() != rel2.getTo() );
        assertTrue( rel.getFrom() != rel2.getFrom() );
        assertEquals( rel, rel2 );
        assertEquals( rel.hashCode(), rel2.hashCode() );
    }

}
