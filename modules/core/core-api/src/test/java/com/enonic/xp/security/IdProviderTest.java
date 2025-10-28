package com.enonic.xp.security;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdProviderTest
{
    @Test
    void testCreateIdProvider()
    {
        final IdProviderKey idProviderKey = IdProviderKey.from( "myIdProvider" );
        final IdProvider idProvider = IdProvider.create().displayName( "my id provider" ).key( idProviderKey ).build();

        assertEquals( "myIdProvider", idProvider.getKey().toString() );
        assertEquals( "my id provider", idProvider.getDisplayName() );
    }

    @Test
    void testCreateIdProviderFromSource()
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
    void testIdProviderKey()
    {
        final IdProviderKey idProviderKey = IdProviderKey.from( "myIdProvider" );
        final IdProviderKey idProviderKey2 = IdProviderKey.from( "myIdProvider" );

        assertEquals( idProviderKey, idProviderKey2 );
        assertEquals( "myIdProvider", idProviderKey.toString() );
    }

    @Test
    void testInvalidIdProviderKeyCharacter1()
    {
        assertThrows(IllegalArgumentException.class, () -> IdProviderKey.from( "my<IdProvider" ));
    }

    @Test
    void testInvalidIdProviderKeyCharacter2()
    {
        assertThrows(IllegalArgumentException.class, () -> IdProviderKey.from( "myUser>Store" ));
    }

    @Test
    void testInvalidIdProviderKeyCharacter3()
    {
        assertThrows(IllegalArgumentException.class, () -> IdProviderKey.from( "myUser\"Store" ));
    }

    @Test
    void testInvalidIdProviderKeyCharacter4()
    {
        assertThrows(IllegalArgumentException.class, () -> IdProviderKey.from( "myUserSt'ore" ));
    }

}
