package com.enonic.xp.security;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrincipalRelationshipTest
{
    private static final PrincipalKey USER = PrincipalKey.ofUser( IdProviderKey.system(), "user" );

    private static final PrincipalKey USER_2 = PrincipalKey.ofUser( IdProviderKey.system(), "user2" );

    private static final PrincipalKey GROUP = PrincipalKey.ofGroup( IdProviderKey.system(), "group" );

    private static final PrincipalKey GROUP_2 = PrincipalKey.ofGroup( IdProviderKey.system(), "group2" );

    private static final PrincipalKey ROLE = PrincipalKey.ofRole( "role" );

    private static final PrincipalKey ROLE_2 = PrincipalKey.ofRole( "role2" );

    @Test
    void testFromToSamePrincipal()
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( GROUP ).to( GROUP )) ;
    }

    // from Role
    @Test
    void testFromRoleToUser()
    {
        PrincipalRelationship rel = PrincipalRelationship.from( ROLE ).to( USER );
        assertEquals( ROLE, rel.getFrom() );
        assertEquals( USER, rel.getTo() );
        assertEquals( "role:role -> user:system:user", rel.toString() );
    }

    @Test
    void testFromRoleToGroup()
    {
        PrincipalRelationship rel = PrincipalRelationship.from( ROLE ).to( GROUP );
        assertEquals( ROLE, rel.getFrom() );
        assertEquals( GROUP, rel.getTo() );
        assertEquals( "role:role -> group:system:group", rel.toString() );
    }

    @Test
    void testFromRoleToRole()
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( ROLE ).to( ROLE_2 ));
    }

    // from Group
    @Test
    void testFromGroupToUser()
    {
        PrincipalRelationship rel = PrincipalRelationship.from( GROUP ).to( USER );
        assertEquals( GROUP, rel.getFrom() );
        assertEquals( USER, rel.getTo() );
        assertEquals( "group:system:group -> user:system:user", rel.toString() );
    }

    @Test
    void testFromGroupToGroup()
    {
        PrincipalRelationship rel = PrincipalRelationship.from( GROUP ).to( GROUP_2 );
        assertEquals( GROUP, rel.getFrom() );
        assertEquals( GROUP_2, rel.getTo() );
        assertEquals( "group:system:group -> group:system:group2", rel.toString() );
    }

    @Test
    void testFromGroupToRole()
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalRelationship.from( GROUP ).to( ROLE ));
    }

    // from User
    @Test
    void testFromUserToUser()
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( USER ).to( USER_2 ));
    }

    @Test
    void testFromUserToGroup()
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( USER ).to( GROUP ) );
    }

    @Test
    void testFromUserToRole()
    {
        assertThrows(IllegalArgumentException.class, () ->  PrincipalRelationship.from( USER ).to( ROLE ));
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( PrincipalRelationship.class ).verify();
    }
}
