package com.enonic.wem.core.lifecycle;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class RunLevelTest
{
    @Test
    public void testAll()
    {
        final List<RunLevel> list = RunLevel.all();

        assertEquals( 5, list.size() );
        assertEquals( RunLevel.L1, list.get( 0 ) );
        assertEquals( RunLevel.L2, list.get( 1 ) );
        assertEquals( RunLevel.L3, list.get( 2 ) );
        assertEquals( RunLevel.L4, list.get( 3 ) );
        assertEquals( RunLevel.L5, list.get( 4 ) );
    }

    @Test
    public void testReverse()
    {
        final List<RunLevel> list = RunLevel.reverse();

        assertEquals( 5, list.size() );
        assertEquals( RunLevel.L1, list.get( 4 ) );
        assertEquals( RunLevel.L2, list.get( 3 ) );
        assertEquals( RunLevel.L3, list.get( 2 ) );
        assertEquals( RunLevel.L4, list.get( 1 ) );
        assertEquals( RunLevel.L5, list.get( 0 ) );
    }
}
