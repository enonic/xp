package com.enonic.wem.core.initializer;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class InitializerTaskTest
{
    @Test
    public void testOrder()
    {
        final InitializerTask task1 = new InitializerTask( 10 )
        {
            @Override
            public void initialize()
                throws Exception
            {
            }
        };

        final InitializerTask task2 = new InitializerTask( 20 )
        {
            @Override
            public void initialize()
                throws Exception
            {
            }
        };

        final InitializerTask task3 = new InitializerTask( 15 )
        {
            @Override
            public void initialize()
                throws Exception
            {
            }
        };

        final List<InitializerTask> list = Lists.newArrayList( task1, task2, task3 );
        Collections.sort( list );

        assertEquals( 3, list.size() );
        assertSame( task1, list.get( 0 ) );
        assertSame( task3, list.get( 1 ) );
        assertSame( task2, list.get( 2 ) );
    }
}
