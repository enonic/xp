package com.enonic.xp.security;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.node.NodePath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrincipalKeyTest
{
    @Test
    void testPrincipalUser()
    {
        final IdProviderKey idProvider = IdProviderKey.from( "myIdProvider" );
        final PrincipalKey user = PrincipalKey.ofUser( idProvider, "userId" );

        assertEquals( "user:myIdProvider:userId", user.toString() );
        assertEquals( "myIdProvider", user.getIdProviderKey().toString() );
        assertEquals( "userId", user.getId() );
        assertEquals( PrincipalType.USER, user.getType() );
        assertTrue( user.isUser() );
        assertFalse( user.isGroup() );
        assertFalse( user.isAnonymous() );
        assertFalse( user.isRole() );
    }

    @Test
    void testPrincipalGroup()
    {
        final IdProviderKey idProvider = IdProviderKey.from( "myIdProvider" );
        final PrincipalKey group = PrincipalKey.ofGroup( idProvider, "groupid" );

        assertEquals( "group:myIdProvider:groupid", group.toString() );
        assertEquals( "myIdProvider", group.getIdProviderKey().toString() );
        assertEquals( "groupid", group.getId() );
        assertEquals( PrincipalType.GROUP, group.getType() );
        assertTrue( group.isGroup() );
        assertFalse( group.isUser() );
        assertFalse( group.isAnonymous() );
        assertFalse( group.isRole() );
    }

    @Test
    void testPrincipalRole()
    {
        final PrincipalKey role = PrincipalKey.ofRole( "roleid" );

        assertEquals( "role:roleid", role.toString() );
        assertNull( role.getIdProviderKey() );
        assertEquals( "roleid", role.getId() );
        assertEquals( PrincipalType.ROLE, role.getType() );
        assertTrue( role.isRole() );
        assertFalse( role.isGroup() );
        assertFalse( role.isUser() );
        assertFalse( role.isAnonymous() );
    }

    @Test
    void testPrincipalAnonymous()
    {
        final PrincipalKey anonymous = PrincipalKey.ofAnonymous();

        assertEquals( "user:system:anonymous", anonymous.toString() );
        assertNotNull( anonymous.getIdProviderKey() );
        assertEquals( "system", anonymous.getIdProviderKey().toString() );
        assertEquals( "anonymous", anonymous.getId() );
        assertEquals( PrincipalType.USER, anonymous.getType() );
        assertTrue( anonymous.isAnonymous() );
        assertFalse( anonymous.isRole() );
        assertFalse( anonymous.isGroup() );
        assertTrue( anonymous.isUser() );
    }

    @Test
    void testIdentityFrom()
    {
        final PrincipalKey anonymous = PrincipalKey.from( "user:system:anonymous" );
        final PrincipalKey user = PrincipalKey.from( "user:myIdProvider:myUser" );
        final PrincipalKey group = PrincipalKey.from( "group:myIdProvider:mygroup" );
        final PrincipalKey role = PrincipalKey.from( "role:myrole" );

        assertEquals( "user:system:anonymous", anonymous.toString() );
        assertEquals( PrincipalKey.ofAnonymous(), anonymous );
        assertEquals( "role:myrole", role.toString() );
        assertEquals( "group:myIdProvider:mygroup", group.toString() );
        assertEquals( "user:myIdProvider:myUser", user.toString() );
    }

    @Test
    void testToPath()
    {
        final PrincipalKey anonymous = PrincipalKey.from( "user:system:anonymous" );
        final PrincipalKey user = PrincipalKey.from( "user:myIdProvider:myUser" );
        final PrincipalKey group = PrincipalKey.from( "group:myIdProvider:mygroup" );
        final PrincipalKey role = PrincipalKey.from( "role:myrole" );

        assertEquals( new NodePath( "/identity/system/users/anonymous" ), anonymous.toPath() );
        assertEquals( new NodePath( "/identity/roles/myrole" ), role.toPath() );
        assertEquals( new NodePath( "/identity/myIdProvider/groups/mygroup" ), group.toPath() );
        assertEquals( new NodePath( "/identity/myIdProvider/users/myUser" ), user.toPath() );
    }

    @Test
    void testInvalidIdentityKey()
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:myUser" ));
    }

    @Test
    void testInvalidIdentityType()
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "anonymous:myIdProvider:anonymous" ));
    }

    @Test
    void testInvalidCharactersInKey1()
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:my<User" ));
    }

    @Test
    void testInvalidCharactersInKey2()
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:my>User" ));
    }

    @Test
    void testInvalidCharactersInKey3()
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:my\"User" ));
    }

    @Test
    void testInvalidCharactersInKey4()
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:my'<User" ));
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( PrincipalKey.class ).verify();
    }
}
