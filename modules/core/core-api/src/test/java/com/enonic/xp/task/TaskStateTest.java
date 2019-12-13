package com.enonic.xp.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskStateTest
{
    @Test
    public void testStates()
    {
        assertEquals( 4, TaskState.values().length );
    }
}
