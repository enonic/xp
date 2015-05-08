package com.enonic.xp.core.impl.i18n;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.i18n.MessageBundle;

import static org.junit.Assert.*;

public class MessageBundleImplTest
{

    private MessageBundle createDefault()
    {
        Properties properties = new Properties();
        properties.put( "key1", "value1" );
        properties.put( "key2", "value1" );
        properties.put( "key3", "value1" );
        properties.put( "key4", "value {} was there" );

        return new MessageBundleImpl( properties );
    }


    @Test
    public void testNonExistingKey()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();

        assertNull( resourceBundle.localize( "dummyKey" ) );

    }

    @Test
    public void testEmptyResourceBundle()
    {
        MessageBundle resourceBundle = new MessageBundleImpl( new Properties() );
        assertNull( resourceBundle.localize( "key1" ) );
    }

    @Test
    @Ignore("This is not implemented as expected yet")
    public void testParameterizedPhrase()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();

        Object[] testArgs = {"number"};

        String resolvedPhrase = resourceBundle.localize( "key4", testArgs );

        assertEquals( "det ble fisket 8 fisk av type torsk med musse p\u00e5 stampen", resolvedPhrase );
    }

    @Test
    @Ignore("This is not implemented as expected yet")
    public void testMissingParametersPhrase()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();

        Object[] testArgs = {"torsk"};

        String resolvedPhrase = resourceBundle.localize( "fiskmessage", testArgs );

        assertEquals( "det ble fisket {1} fisk av type torsk med musse p\u00e5 stampen", resolvedPhrase );
    }

    @Test
    @Ignore("This is not implemented as expected yet")
    public void testNullParametersPhrase()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();

        String resolvedPhrase = resourceBundle.localize( "fiskmessage", null );

        assertEquals( "det ble fisket {1} fisk av type {0} med musse p\u00e5 stampen", resolvedPhrase );
    }
}
