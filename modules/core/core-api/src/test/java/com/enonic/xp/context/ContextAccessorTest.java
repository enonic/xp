package com.enonic.xp.context;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ContextAccessorTest
{
    @Test
    void testCurrent()
    {
        assertNotNull( ContextAccessor.current() );

        final Context context = Mockito.mock( Context.class );
        ScopedValue.where( ContextAccessor.INSTANCE, context ).run( () -> assertSame( context, ContextAccessor.current() ) );

        assertNotNull( ContextAccessor.current() );
    }
}
