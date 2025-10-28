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
        ContextAccessor.INSTANCE.remove();
        assertNotNull( ContextAccessor.current() );

        final Context context = Mockito.mock( Context.class );
        ContextAccessor.INSTANCE.set( context );
        assertSame( context, ContextAccessor.current() );
        ContextAccessor.INSTANCE.remove();
    }
}
