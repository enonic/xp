package com.enonic.xp.security;

import org.junit.Test;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.UserStoreKey;

import static org.junit.Assert.*;

public class PrincipalKeyTest
{


    @Test
    public void testPrincipalUser()
        throws Exception
    {
        final UserStoreKey userStore = new UserStoreKey( "myUserStore" );
        final PrincipalKey user = PrincipalKey.ofUser( userStore, "userId" );

        assertEquals( "user:myUserStore:userId", user.toString() );
        assertEquals( "myUserStore", user.getUserStore().toString() );
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
        final UserStoreKey userStore = new UserStoreKey( "myUserStore" );
        final PrincipalKey group = PrincipalKey.ofGroup( userStore, "groupid" );

        assertEquals( "group:myUserStore:groupid", group.toString() );
        assertEquals( "myUserStore", group.getUserStore().toString() );
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
        assertNull( role.getUserStore() );
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
        assertNotNull( anonymous.getUserStore() );
        assertEquals( "system", anonymous.getUserStore().toString() );
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
        final PrincipalKey user = PrincipalKey.from( "user:myUserStore:myUser" );
        final PrincipalKey group = PrincipalKey.from( "group:myUserStore:mygroup" );
        final PrincipalKey role = PrincipalKey.from( "role:myrole" );

        assertEquals( "user:system:anonymous", anonymous.toString() );
        assertEquals( PrincipalKey.ofAnonymous(), anonymous );
        assertEquals( "role:myrole", role.toString() );
        assertEquals( "group:myUserStore:mygroup", group.toString() );
        assertEquals( "user:myUserStore:myUser", user.toString() );
    }

    @Test
    public void testToPath()
        throws Exception
    {
        final PrincipalKey anonymous = PrincipalKey.from( "user:system:anonymous" );
        final PrincipalKey user = PrincipalKey.from( "user:myUserStore:myUser" );
        final PrincipalKey group = PrincipalKey.from( "group:myUserStore:mygroup" );
        final PrincipalKey role = PrincipalKey.from( "role:myrole" );

        assertEquals( new NodePath( "/system/users/anonymous" ), anonymous.toPath() );
        assertEquals( new NodePath( "/roles/myrole" ), role.toPath() );
        assertEquals( new NodePath( "/myUserStore/groups/mygroup" ), group.toPath() );
        assertEquals( new NodePath( "/myUserStore/users/myUser" ), user.toPath() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIdentityKey()
        throws Exception
    {
        PrincipalKey.from( "user:myUser" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIdentityType()
        throws Exception
    {
        PrincipalKey.from( "anonymous:myUserStore:anonymous" );
    }
}