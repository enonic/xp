package com.enonic.xp.core.impl.i18n;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.enonic.xp.i18n.MessageBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MessageBundleImplTest
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
        return new MessageBundleImpl( properties, Locale.ENGLISH );
    }

    @Test
    void testNonExistingKey()
    {
        MessageBundle resourceBundle = createDefault();
        assertNull( resourceBundle.localize( "dummyKey" ) );
    }

    @Test
    void testEmptyValue()
    {
        MessageBundle resourceBundle = createDefault();
        assertNull( resourceBundle.localize( "key6" ) );
    }

    @Test
    void testEmptyResourceBundle()
    {
        MessageBundle resourceBundle = new MessageBundleImpl( new Properties(), Locale.ENGLISH );
        assertNull( resourceBundle.localize( "dummyKey" ) );
    }

    @Test
    void dateTime()
    {
        final Properties properties = new Properties();
        properties.put( "key1", "{0,time,short} {0,date,short}" );
        MessageBundle resourceBundle = new MessageBundleImpl( properties, Locale.TRADITIONAL_CHINESE );
        assertEquals( "上午9:46 1973/3/3", resourceBundle.localize( "key1", Instant.ofEpochMilli( 100000000000L ).toEpochMilli() ) );
    }

    @Test
    void nullArguments()
    {
        final Properties properties = new Properties();
        properties.put( "key1", "{0}" );
        MessageBundle resourceBundle = new MessageBundleImpl( properties, Locale.ROOT );
        assertEquals( "{0}", resourceBundle.localize( "key1", (Object[]) null ) );
    }

    @Test
    void nullArgument()
    {
        final Properties properties = new Properties();
        properties.put( "key1", "{0}" );
        MessageBundle resourceBundle = new MessageBundleImpl( properties, Locale.ROOT );
        assertEquals( "null", resourceBundle.localize( "key1", (Object) null ) );
    }

    @Test
    void localDateTime()
    {
        LocalTime lt = LocalTime.of( 13, 56, 4 );
        LocalDate ld = LocalDate.of( 2021, 5, 3 );

        long ltms = lt.atDate( LocalDate.EPOCH ).atZone( ZoneOffset.UTC ).toInstant().toEpochMilli();
        long ldms = ld.atTime( LocalTime.MIN ).atZone( ZoneOffset.UTC ).toInstant().toEpochMilli();

        final Properties properties = new Properties();
        properties.put( "key1", "At {1,time,medium} on {0,date,short}" );
        MessageBundle resourceBundle = new MessageBundleImpl( properties, Locale.US );
        // https://bugs.openjdk.org/browse/JDK-8304925
        assertEquals( "At 1:56:04\u202FPM on 5/3/21", resourceBundle.localize( "key1", ldms, ltms ) );
    }

    @Test
    void alwaysUTC()
    {
        final Properties properties = new Properties();
        properties.put( "key1", "{0,date,z}" );
        MessageBundle resourceBundle = new MessageBundleImpl( properties, Locale.ROOT );
        assertEquals( "UTC", resourceBundle.localize( "key1", System.currentTimeMillis() ) );
    }

    @Test
    void testAsMap()
    {
        final MessageBundle resourceBundle = createDefault();
        final Map<String, String> map = resourceBundle.asMap();

        assertEquals( 6, map.size() );
    }

    @Test
    void testParameterizedPhrase()
    {
        MessageBundle resourceBundle = createDefault();

        Object[] testArgs = {"myValue1"};

        String resolvedPhrase = resourceBundle.localize( "key4", testArgs );

        assertEquals( "value is here myValue1", resolvedPhrase );
    }

    @Test
    void testParameterizedPhraseMissingParameter()
    {
        MessageBundle resourceBundle = createDefault();

        Object[] testArgs = {"myValue1"};

        String resolvedPhrase = resourceBundle.localize( "key5", testArgs );

        assertEquals( "value is here myValue1 and there {1}", resolvedPhrase );
    }

    @Test
    void testParameterizedPhrase_two_values()
    {
        MessageBundle resourceBundle = createDefault();

        Object[] testArgs = {"myValue1", "myValue2"};

        String resolvedPhrase = resourceBundle.localize( "key5", testArgs );

        assertEquals( "value is here myValue1 and there myValue2", resolvedPhrase );
    }
}
