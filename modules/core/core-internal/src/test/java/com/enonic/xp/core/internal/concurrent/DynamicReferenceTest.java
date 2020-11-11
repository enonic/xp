package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeout;

class DynamicReferenceTest
{
    @Test
    void getNow()
    {
        final DynamicReference<Object> reference = new DynamicReference<>();
        assertNull( reference.getNow( null ) );

        reference.set( Boolean.TRUE );
        assertEquals( Boolean.TRUE, reference.getNow( null ) );
    }

    @Test
    void get()
    {
        final DynamicReference<Object> reference = new DynamicReference<>();
        final Object object = new Object();
        CompletableFuture.runAsync( () -> reference.set( object ), CompletableFuture.delayedExecutor( 1, TimeUnit.SECONDS ) );

        assertTimeout( Duration.ofMinutes( 1 ), () -> assertSame( object, reference.get( 1, TimeUnit.HOURS ) ) );
    }

    @Test
    void get_second()
        throws Exception
    {
        final DynamicReference<Object> reference = new DynamicReference<>();
        final Object object = new Object();

        reference.set( new Object() ); // NOT expected to get this object
        reference.set( object ); // expected to get this object

        assertSame( object, reference.get( 1, TimeUnit.HOURS ) );
    }

    @Test
    void get_after_reset()
        throws Exception
    {
        final DynamicReference<Object> reference = new DynamicReference<>();
        final Object object = new Object();

        reference.set( new Object() ); // NOT expected to get this object
        reference.reset();
        reference.set( object ); // expected to get this object

        assertSame( object, reference.get( 1, TimeUnit.HOURS ) );
    }

    @Test
    void get_after_double_reset()
        throws Exception
    {
        final DynamicReference<Object> reference = new DynamicReference<>();
        final Object object = new Object();

        reference.set( new Object() ); // NOT expected to get this object
        reference.reset();
        reference.reset();
        reference.set( object ); // expected to get this object

        assertSame( object, reference.get( 1, TimeUnit.HOURS ) );
    }
}
