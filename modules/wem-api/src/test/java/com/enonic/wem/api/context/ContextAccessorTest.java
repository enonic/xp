package com.enonic.wem.api.context;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

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
