package com.enonic.xp.core.impl.i18n;

import java.util.Map;
import java.util.Properties;

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
        properties.put( "key4", "value is here {0}" );
        properties.put( "key5", "value is here {0} and there {1}" );
        properties.put( "key6", "" );
        return new MessageBundleImpl( properties );
    }

    @Test
    public void testNonExistingKey()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();
        assertEquals( resourceBundle.localize( "dummyKey" ), null );
    }

    @Test
    public void testEmptyValue()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();
        assertEquals( resourceBundle.localize( "key6" ), null );
    }

    @Test
    public void testEmptyResourceBundle()
    {
        MessageBundle resourceBundle = new MessageBundleImpl( new Properties() );
        assertEquals( resourceBundle.localize( "dummyKey" ), null );
    }

    @Test
    public void testAsMap()
    {
        final MessageBundle resourceBundle = createDefault();
        final Map<String, String> map = resourceBundle.asMap();

        assertEquals( 6, map.size() );
    }

    @Test
    public void testParameterizedPhrase()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();

        Object[] testArgs = {"myValue1"};

        String resolvedPhrase = resourceBundle.localize( "key4", testArgs );

        assertEquals( "value is here myValue1", resolvedPhrase );
    }

    @Test
    public void testParameterizedPhraseMissingParameter()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();

        Object[] testArgs = {"myValue1"};

        String resolvedPhrase = resourceBundle.localize( "key5", testArgs );

        assertEquals( "value is here myValue1 and there {1}", resolvedPhrase );
    }

    @Test
    public void testParameterizedPhrase_two_values()
        throws Exception
    {
        MessageBundle resourceBundle = createDefault();

        Object[] testArgs = {"myValue1", "myValue2"};

        String resolvedPhrase = resourceBundle.localize( "key5", testArgs );

        assertEquals( "value is here myValue1 and there myValue2", resolvedPhrase );
    }
}
