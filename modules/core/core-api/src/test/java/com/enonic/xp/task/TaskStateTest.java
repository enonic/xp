package com.enonic.xp.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskStateTest
{
    @Test
    void testStates()
    {
        assertEquals( 4, TaskState.values().length );
    }
}
