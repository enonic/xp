package com.enonic.xp.core.impl.i18n;

import java.util.Properties;

import org.junit.Test;

import com.enonic.xp.i18n.MessageBundle;

import static org.junit.Assert.*;

public class MessageBundleImplTest
{

    private static final String NORWEGIAN = "\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5";


    @Test
    public void testNorwegianCharacters()
        throws Exception
    {
        MessageBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();
        assertEquals( NORWEGIAN, resourceBundle.localize( "norsketegn" ) );
    }

    @Test
    public void testResourceOrdering()
        throws Exception
    {
        MessageBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        assertEquals( resourceBundle.localize( "only_in_en-us" ), "en-us" );
        assertEquals( resourceBundle.localize( "in_all" ), "en-us" );
        assertEquals( resourceBundle.localize( "no_and_default" ), "no" );
        assertEquals( resourceBundle.localize( "only_in_default" ), "default" );
    }

    @Test
    public void testNonExistingKey()
        throws Exception
    {
        MessageBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        assertNull( resourceBundle.localize( "in_all_not" ) );
        assertNotNull( resourceBundle.localize( "in_all" ) );
        assertNull( resourceBundle.localize( "only_in_en" ) );
        assertNotNull( resourceBundle.localize( "only_in_en-us" ) );
    }

    @Test
    public void testEmptyResourceBundle()
    {
        MessageBundle resourceBundle = new MessageBundleImpl( new Properties() );
        assertNull( resourceBundle.localize( "in_all" ) );
    }

    @Test
    public void testParameterizedPhrase()
        throws Exception
    {
        MessageBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        Object[] testArgs = {"torsk", 8};

        String resolvedPhrase = resourceBundle.localize( "fiskmessage", testArgs );

        assertEquals( "det ble fisket 8 fisk av type torsk med musse p\u00e5 stampen", resolvedPhrase );
    }

    @Test
    public void testMissingParametersPhrase()
        throws Exception
    {
        MessageBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        Object[] testArgs = {"torsk"};

        String resolvedPhrase = resourceBundle.localize( "fiskmessage", testArgs );

        assertEquals( "det ble fisket {1} fisk av type torsk med musse p\u00e5 stampen", resolvedPhrase );
    }

    @Test
    public void testNullParametersPhrase()
        throws Exception
    {
        MessageBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        String resolvedPhrase = resourceBundle.localize( "fiskmessage", null );

        assertEquals( "det ble fisket {1} fisk av type {0} med musse p\u00e5 stampen", resolvedPhrase );
    }
}
