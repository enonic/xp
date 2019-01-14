package com.enonic.xp.security;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;

import static org.junit.Assert.*;

public class IdProviderTest
{
    @Test
    public void testCreateIdProvider()
        throws Exception
    {
        final IdProviderKey idProviderKey = IdProviderKey.from( "myIdProvider" );
        final IdProvider idProvider = IdProvider.create().displayName( "my id provider" ).key( idProviderKey ).build();

        assertEquals( "myIdProvider", idProvider.getKey().toString() );
        assertEquals( "my id provider", idProvider.getDisplayName() );
    }

    @Test
    public void testCreateIdProviderFromSource()
        throws Exception
    {
        final IdProviderKey key = IdProviderKey.from( "myIdProvider" );
        final String displayName = "My id provider";
        final String description = "Description";
        final IdProviderConfig idProviderConfig =
            IdProviderConfig.create().applicationKey( ApplicationKey.SYSTEM ).config( new PropertyTree() ).build();

        final IdProvider source = IdProvider.create().key( key ).displayName( displayName ).description( description ).idProviderConfig(
            idProviderConfig ).build();
        final IdProvider idProvider = IdProvider.create( source ).build();

        assertTrue( idProvider.getKey().equals( key ) );
        assertEquals( displayName, idProvider.getDisplayName() );
        assertEquals( description, idProvider.getDescription() );
        assertTrue( idProvider.getIdProviderConfig().equals( idProviderConfig ) );
    }

    @Test
    public void testIdProviderKey()
        throws Exception
    {
        final IdProviderKey idProviderKey = IdProviderKey.from( "myIdProvider" );
        final IdProviderKey idProviderKey2 = IdProviderKey.from( "myIdProvider" );

        assertEquals( idProviderKey, idProviderKey2 );
        assertEquals( "myIdProvider", idProviderKey.toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIdProviderKeyCharacter1()
        throws Exception
    {
        IdProviderKey.from( "my<IdProvider" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIdProviderKeyCharacter2()
        throws Exception
    {
        IdProviderKey.from( "myUser>Store" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIdProviderKeyCharacter3()
        throws Exception
    {
        IdProviderKey.from( "myUser\"Store" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIdProviderKeyCharacter4()
        throws Exception
    {
        IdProviderKey.from( "myUserSt'ore" );
    }

}
