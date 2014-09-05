package com.enonic.wem.core.lifecycle;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class LifecycleStageTest
{
    @Test
    public void testAll()
    {
        final List<LifecycleStage> list = LifecycleStage.all();

        assertEquals( 5, list.size() );
        assertEquals( LifecycleStage.L1, list.get( 0 ) );
        assertEquals( LifecycleStage.L2, list.get( 1 ) );
        assertEquals( LifecycleStage.L3, list.get( 2 ) );
        assertEquals( LifecycleStage.L4, list.get( 3 ) );
        assertEquals( LifecycleStage.L5, list.get( 4 ) );
    }

    @Test
    public void testReverse()
    {
        final List<LifecycleStage> list = LifecycleStage.reverse();

        assertEquals( 5, list.size() );
        assertEquals( LifecycleStage.L1, list.get( 4 ) );
        assertEquals( LifecycleStage.L2, list.get( 3 ) );
        assertEquals( LifecycleStage.L3, list.get( 2 ) );
        assertEquals( LifecycleStage.L4, list.get( 1 ) );
        assertEquals( LifecycleStage.L5, list.get( 0 ) );
    }
}
