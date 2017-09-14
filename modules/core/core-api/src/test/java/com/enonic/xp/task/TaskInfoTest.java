package com.enonic.xp.task;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskInfoTest
{
    @Test
    public void testAccessors()
    {
        final TaskId id = TaskId.from( "123" );
        final TaskProgress progress = TaskProgress.EMPTY;

        final TaskInfo info = TaskInfo.create().
            id( id ).
            name( "name" ).
            description( "test" ).
            state( TaskState.FINISHED ).
            progress( progress ).
            build();

        assertEquals( id, info.getId() );
        assertEquals( "name", info.getName() );
        assertEquals( "test", info.getDescription() );
        assertEquals( TaskState.FINISHED, info.getState() );
        assertEquals( progress, info.getProgress() );
    }

    @Test
    public void testState()
    {
        final TaskInfo info1 = TaskInfo.create().
            id( TaskId.from( "123" ) ).
            state( TaskState.RUNNING ).
            build();

        assertEquals( TaskState.RUNNING, info1.getState() );
        assertEquals( true, info1.isRunning() );
        assertEquals( false, info1.isDone() );

        final TaskInfo info2 = info1.copy().
            state( TaskState.FINISHED ).
            build();

        assertEquals( TaskState.FINISHED, info2.getState() );
        assertEquals( false, info2.isRunning() );
        assertEquals( true, info2.isDone() );
    }

    @Test
    public void testCopy()
    {
        final TaskInfo i1 = TaskInfo.create().id( TaskId.from( "123" ) ).build();
        final TaskInfo i2 = i1.copy().description( "test" ).build();

        assertEquals( false, i1.equals( i2 ) );
    }

    @Test
    public void testEquals()
    {
        final TaskInfo i1 = TaskInfo.create().id( TaskId.from( "123" ) ).build();
        final TaskInfo i2 = TaskInfo.create().id( TaskId.from( "123" ) ).build();
        final TaskInfo i3 = TaskInfo.create().id( TaskId.from( "321" ) ).build();

        assertEquals( true, i1.equals( i2 ) );
        assertEquals( false, i1.equals( i3 ) );
        assertEquals( false, i1.equals( "test" ) );
    }

    @Test
    public void testHashCode()
    {
        final TaskInfo i1 = TaskInfo.create().id( TaskId.from( "123" ) ).build();
        final TaskInfo i2 = TaskInfo.create().id( TaskId.from( "123" ) ).build();
        final TaskInfo i3 = TaskInfo.create().id( TaskId.from( "321" ) ).build();

        assertEquals( i1.hashCode(), i2.hashCode() );
        assertNotEquals( i1.hashCode(), i3.hashCode() );
    }

    @Test
    public void testToString()
    {
        final TaskInfo i = TaskInfo.create().id( TaskId.from( "123" ) ).build();
        assertEquals( "TaskInfo{id=123, name=task-123, description=, state=WAITING, progress=TaskProgress{current=0, total=0, info=}}",
                      i.toString() );
    }
}
