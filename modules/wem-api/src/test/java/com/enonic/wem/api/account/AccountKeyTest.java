package com.enonic.wem.api.account;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccountKeyTest
{
    @Test
    public void testUser()
    {
        final AccountKey ref = AccountKey.from( "user:other:dummy" );
        assertNotNull( ref );
        assertEquals( AccountType.USER, ref.getType() );
        assertEquals( "other", ref.getUserStore() );
        assertEquals( "dummy", ref.getLocalName() );
        assertEquals( "other:dummy", ref.getQualifiedName() );
        assertEquals( "user:other:dummy", ref.toString() );
        assertEquals( true, ref.isUser() );
        assertEquals( false, ref.isGroup() );
        assertEquals( false, ref.isRole() );
        assertEquals( false, ref.isSuperUser() );
        assertEquals( false, ref.isAnonymous() );
        assertEquals( false, ref.isBuiltIn() );
    }

    @Test
    public void testGroup()
    {
        final AccountKey ref = AccountKey.from( "group:other:dummy" );
        assertNotNull( ref );
        assertEquals( AccountType.GROUP, ref.getType() );
        assertEquals( "other", ref.getUserStore() );
        assertEquals( "dummy", ref.getLocalName() );
        assertEquals( "other:dummy", ref.getQualifiedName() );
        assertEquals( "group:other:dummy", ref.toString() );
        assertEquals( false, ref.isUser() );
        assertEquals( true, ref.isGroup() );
        assertEquals( false, ref.isRole() );
        assertEquals( false, ref.isSuperUser() );
        assertEquals( false, ref.isAnonymous() );
        assertEquals( false, ref.isBuiltIn() );
    }

    @Test
    public void testRole()
    {
        final AccountKey ref = AccountKey.from( "role:other:dummy" );
        assertNotNull( ref );
        assertEquals( AccountType.ROLE, ref.getType() );
        assertEquals( "other", ref.getUserStore() );
        assertEquals( "dummy", ref.getLocalName() );
        assertEquals( "other:dummy", ref.getQualifiedName() );
        assertEquals( "role:other:dummy", ref.toString() );
        assertEquals( false, ref.isUser() );
        assertEquals( false, ref.isGroup() );
        assertEquals( true, ref.isRole() );
        assertEquals( false, ref.isSuperUser() );
        assertEquals( false, ref.isAnonymous() );
        assertEquals( true, ref.isBuiltIn() );
    }

    @Test
    public void testSuperUser()
    {
        final AccountKey ref = AccountKey.superUser();
        assertNotNull( ref );
        assertEquals( AccountType.USER, ref.getType() );
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