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

public class PrincipalKeyTest
{
    @Test
    public void testPrincipalUser()
        throws Exception
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
    public void testPrincipalGroup()
        throws Exception
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
    public void testPrincipalRole()
        throws Exception
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
    public void testPrincipalAnonymous()
        throws Exception
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
    public void testIdentityFrom()
        throws Exception
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
    public void testToPath()
        throws Exception
    {
        final PrincipalKey anonymous = PrincipalKey.from( "user:system:anonymous" );
        final PrincipalKey user = PrincipalKey.from( "user:myIdProvider:myUser" );
        final PrincipalKey group = PrincipalKey.from( "group:myIdProvider:mygroup" );
        final PrincipalKey role = PrincipalKey.from( "role:myrole" );
        final PrincipalKey serviceAccount = PrincipalKey.from( "sa:myIdProvider:enonic.service.account" );

        assertEquals( new NodePath( "/identity/system/users/anonymous" ), anonymous.toPath() );
        assertEquals( new NodePath( "/identity/roles/myrole" ), role.toPath() );
        assertEquals( new NodePath( "/identity/myIdProvider/groups/mygroup" ), group.toPath() );
        assertEquals( new NodePath( "/identity/myIdProvider/users/myUser" ), user.toPath() );
        assertEquals( new NodePath( "/identity/myIdProvider/service-accounts/enonic.service.account" ), serviceAccount.toPath() );
    }

    @Test
    public void testInvalidIdentityKey()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:myUser" ));
    }

    @Test
    public void testInvalidIdentityType()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "anonymous:myIdProvider:anonymous" ));
    }

    @Test
    public void testInvalidCharactersInKey1()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:my<User" ));
    }

    @Test
    public void testInvalidCharactersInKey2()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:my>User" ));
    }

    @Test
    public void testInvalidCharactersInKey3()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:my\"User" ));
    }

    @Test
    public void testInvalidCharactersInKey4()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> PrincipalKey.from( "user:my'<User" ));
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( PrincipalKey.class ).verify();
    }
}
