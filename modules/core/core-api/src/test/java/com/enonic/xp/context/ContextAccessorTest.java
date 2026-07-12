package com.enonic.xp.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class ContextAccessorTest
{
    @AfterEach
    void tearDown()
    {
        ContextAccessor.LEGACY.remove();
    }

    @Test
    void currentReturnsDefaultWhenUnbound()
    {
        assertNotNull( ContextAccessor.current() );
        assertNotSame( ContextAccessor.current(), ContextAccessor.current() );
    }

    @Test
    void currentReturnsBoundContext()
    {
        final Context context = Mockito.mock( Context.class );
        ScopedValue.where( ContextAccessor.INSTANCE, context ).run( () -> assertSame( context, ContextAccessor.current() ) );

        assertNotNull( ContextAccessor.current() );
    }

    @Test
    void currentFallsBackToLegacy()
    {
        final Context legacy = Mockito.mock( Context.class );
        ContextAccessor.LEGACY.set( legacy );

        assertSame( legacy, ContextAccessor.current() );

        ContextAccessor.LEGACY.remove();
        assertNotNull( ContextAccessor.current() );
        assertNotSame( legacy, ContextAccessor.current() );
    }

    @Test
    void boundContextTakesPrecedenceOverLegacyAndDoesNotClobberIt()
    {
        final Context legacy = Mockito.mock( Context.class );
        final Context scoped = Mockito.mock( Context.class );

        ContextAccessor.LEGACY.set( legacy );

        ScopedValue.where( ContextAccessor.INSTANCE, scoped ).run( () -> assertSame( scoped, ContextAccessor.current() ) );

        assertSame( legacy, ContextAccessor.current() );
    }
}
