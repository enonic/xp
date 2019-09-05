package com.enonic.xp.context;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class ContextAccessorTest
{
    @Test
    public void testCurrent()
    {
        ContextAccessor.INSTANCE.remove();
        assertNotNull( ContextAccessor.current() );

        final Context context = Mockito.mock( Context.class );
        ContextAccessor.INSTANCE.set( context );
        assertSame( context, ContextAccessor.current() );
    }
}
