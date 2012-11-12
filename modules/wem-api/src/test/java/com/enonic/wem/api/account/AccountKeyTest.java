package com.enonic.wem.api.account;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccountKeyTest
{
    @Test
    public void testUser()
    {
        final AccountKey key1 = AccountKey.from( "user:other:dummy" );
        testUser( key1 );

        final AccountKey key2 = AccountKey.user( "other:dummy" );
        testUser( key2 );
    }

    private void testUser( final AccountKey key )
    {
        assertNotNull( key );
        assertEquals( AccountType.USER, key.getType() );
        assertTrue( key instanceof UserKey );
        assertEquals( "other", key.getUserStore() );
        assertEquals( "dummy", key.getLocalName() );
        assertEquals( "other:dummy", key.getQualifiedName() );
        assertEquals( "user:other:dummy", key.toString() );
        assertEquals( true, key.isUser() );
        assertEquals( false, key.isGroup() );
        assertEquals( false, key.isRole() );
        assertEquals( false, key.isSuperUser() );
        assertEquals( false, key.isAnonymous() );
        assertEquals( false, key.isBuiltIn() );
    }

    @Test
    public void testGroup()
    {
        final AccountKey key1 = AccountKey.from( "group:other:dummy" );
        testGroup( key1 );

        final AccountKey key2 = AccountKey.group( "other:dummy" );
        testGroup( key2 );
    }

    private void testGroup( final AccountKey key )
    {
        assertNotNull( key );
        assertEquals( AccountType.GROUP, key.getType() );
        assertTrue( key instanceof GroupKey );
        assertEquals( "other", key.getUserStore() );
        assertEquals( "dummy", key.getLocalName() );
        assertEquals( "other:dummy", key.getQualifiedName() );
        assertEquals( "group:other:dummy", key.toString() );
        assertEquals( false, key.isUser() );
        assertEquals( true, key.isGroup() );
        assertEquals( false, key.isRole() );
        assertEquals( false, key.isSuperUser() );
        assertEquals( false, key.isAnonymous() );
        assertEquals( false, key.isBuiltIn() );
    }

    @Test
    public void testRole()
    {
        final AccountKey key1 = AccountKey.from( "role:other:dummy" );
        testRole( key1 );

        final AccountKey key2 = AccountKey.role( "other:dummy" );
        testRole( key2 );
    }

    private void testRole( final AccountKey key )
    {
        assertNotNull( key );
        assertEquals( AccountType.ROLE, key.getType() );
        assertTrue( key instanceof RoleKey );
        assertEquals( "other", key.getUserStore() );
        assertEquals( "dummy", key.getLocalName() );
        assertEquals( "other:dummy", key.getQualifiedName() );
        assertEquals( "role:other:dummy", key.toString() );
        assertEquals( false, key.isUser() );
        assertEquals( false, key.isGroup() );
        assertEquals( true, key.isRole() );
        assertEquals( false, key.isSuperUser() );
        assertEquals( false, key.isAnonymous() );
        assertEquals( true, key.isBuiltIn() );
    }

    @Test
    public void testSuperUser()
    {
        final AccountKey ref = AccountKey.superUser();
        assertNotNull( ref );
        assertEquals( AccountType.USER, ref.getType() );
        assertTrue( ref instanceof UserKey );
        assertEquals( "system", ref.getUserStore() );
        assertEquals( "admin", ref.getLocalName() );
        assertEquals( "system:admin", ref.getQualifiedName() );
        assertEquals( "user:system:admin", ref.toString() );
        assertEquals( true, ref.isUser() );
        assertEquals( false, ref.isGroup() );
        assertEquals( false, ref.isRole() );
        assertEquals( true, ref.isSuperUser() );
        assertEquals( false, ref.isAnonymous() );
        assertEquals( true, ref.isBuiltIn() );
    }

    @Test
    public void testAnonymous()
    {
        final AccountKey ref = AccountKey.anonymous();
        assertNotNull( ref );
        assertEquals( AccountType.USER, ref.getType() );
        assertTrue( ref instanceof UserKey );
        assertEquals( "system", ref.getUserStore() );
        assertEquals( "anonymous", ref.getLocalName() );
        assertEquals( "system:anonymous", ref.getQualifiedName() );
        assertEquals( "user:system:anonymous", ref.toString() );
        assertEquals( true, ref.isUser() );
        assertEquals( false, ref.isGroup() );
        assertEquals( false, ref.isRole() );
        assertEquals( false, ref.isSuperUser() );
        assertEquals( true, ref.isAnonymous() );
        assertEquals( true, ref.isBuiltIn() );
    }

    @Test
    public void testHashCode()
    {
        final AccountKey ref1 = AccountKey.from( "user:system:admin" );
        final AccountKey ref2 = AccountKey.superUser();
        final AccountKey ref3 = AccountKey.from( "role:other:dummy" );

        assertTrue( ref1.hashCode() == ref2.hashCode() );
        assertFalse( ref1.hashCode() == ref3.hashCode() );
    }

    @Test
    public void testEquals()
    {
        final AccountKey ref1 = AccountKey.from( "user:system:admin" );
        final AccountKey ref2 = AccountKey.superUser();
        final AccountKey ref3 = AccountKey.from( "role:other:dummy" );

        assertTrue( ref1.equals( ref2 ) );
        assertFalse( ref1.equals( ref3 ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRef_wrongType()
    {
        AccountKey.from( "product:other:dummy" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRef_empty()
    {
        AccountKey.from( "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRef_null()
    {
        AccountKey.from( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRef_onlyOnePart()
    {
        AccountKey.from( "user" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRef_onlyTwoParts()
    {
        AccountKey.from( "user:other" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRef_tooManyParts()
    {
        AccountKey.from( "user:other:dummy:string" );
    }
}