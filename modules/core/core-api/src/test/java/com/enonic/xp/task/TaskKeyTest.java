package com.enonic.xp.task;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class TaskKeyTest
{
    @Test
    public void testName()
    {
        final TaskKey taskKey = TaskKey.from( ApplicationKey.from( "my-app" ), "tasks1" );
        assertEquals( "my-app:tasks1", taskKey.toString() );
        assertEquals( "tasks1", taskKey.getName() );
        assertEquals( ApplicationKey.from( "my-app" ), taskKey.getApplicationKey() );
    }

    @Test
    public void testFrom()
    {
        final TaskKey taskKey1 = TaskKey.from( ApplicationKey.from( "my-app" ), "tasks1" );
        assertEquals( "my-app:tasks1", taskKey1.toString() );

        final TaskKey taskKey2 = TaskKey.from( "my-app:tasks1" );
        assertEquals( "my-app:tasks1", taskKey2.toString() );
    }

    @Test
    public void testEquals()
    {
        final TaskKey taskKey1 = TaskKey.from( ApplicationKey.from( "my-app" ), "tasks1" );
        final TaskKey taskKey2 = TaskKey.from( "my-app:tasks1" );

        assertEquals( taskKey1, taskKey2 );
        assertEquals( taskKey1, taskKey1 );
        assertNotEquals( taskKey1, "my-app:tasks1" );
    }

    @Test
    public void testHashCode()
    {
        final TaskKey taskKey1 = TaskKey.from( ApplicationKey.from( "my-app" ), "tasks1" );
        final TaskKey taskKey2 = TaskKey.from( "my-app:tasks1" );
        final TaskKey taskKey3 = TaskKey.from( "my-app:tasks3" );

        assertEquals( taskKey1.hashCode(), taskKey2.hashCode() );
        assertEquals( taskKey1.hashCode(), taskKey1.hashCode() );
        assertNotEquals( taskKey1.hashCode(), taskKey3.hashCode() );
    }
}
