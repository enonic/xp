package com.enonic.wem.api.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrincipalKeyTest
{


    @Test
    public void testPrincipalUser()
        throws Exception
    {
        final UserStoreKey userStore = new UserStoreKey( "myUserStore" );
        final PrincipalKey user = PrincipalKey.ofUser( userStore, "userId" );

        assertEquals( "myUserStore:user:userId", user.toString() );
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

        assertEquals( "myUserStore:group:groupid", group.toString() );
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

        assertEquals( "system:role:roleid", role.toString() );
        assertEquals( "system", role.getUserStore().toString() );
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

        assertEquals( "system:user:anonymous", anonymous.toString() );
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
        final PrincipalKey anonymous = PrincipalKey.from( "system:user:anonymous" );
        final PrincipalKey user = PrincipalKey.from( "myUserStore:user:myUser" );
        final PrincipalKey group = PrincipalKey.from( "myUserStore:group:mygroup" );
        final PrincipalKey role = PrincipalKey.from( "system:role:myrole" );

        assertEquals( "system:user:anonymous", anonymous.toString() );
        assertEquals( PrincipalKey.ofAnonymous(), anonymous );
        assertEquals( "system:role:myrole", role.toString() );
        assertEquals( "myUserStore:group:mygroup", group.toString() );
        assertEquals( "myUserStore:user:myUser", user.toString() );
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
        PrincipalKey.from( "myUserStore:anonymous:anonymous" );
    }
}