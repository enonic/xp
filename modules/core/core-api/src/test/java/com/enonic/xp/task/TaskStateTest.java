package com.enonic.xp.task;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskStateTest
{
    @Test
    public void testStates()
    {
        assertEquals( 4, TaskState.values().length );
    }
}
