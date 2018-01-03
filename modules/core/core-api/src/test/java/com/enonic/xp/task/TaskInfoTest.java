package com.enonic.xp.task;

import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKey;

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
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            progress( progress ).
            build();

        assertEquals( id, info.getId() );
        assertEquals( "name", info.getName() );
        assertEquals( "test", info.getDescription() );
        assertEquals( TaskState.FINISHED, info.getState() );
        assertEquals( progress, info.getProgress() );
        assertEquals( ApplicationKey.from( "com.enonic.myapp" ), info.getApplication() );
        assertEquals( PrincipalKey.from( "user:store:me" ), info.getUser() );
        assertEquals( Instant.parse( "2017-10-01T09:00:00Z" ), info.getStartTime() );
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
        final Instant t = Instant.parse( "2017-10-01T09:00:00Z" );

        final TaskInfo i1 = TaskInfo.create().id( TaskId.from( "123" ) ).startTime( t ).build();
        final TaskInfo i2 = TaskInfo.create().id( TaskId.from( "123" ) ).startTime( t ).build();
        final TaskInfo i3 = TaskInfo.create().id( TaskId.from( "321" ) ).startTime( t ).build();

        final TaskInfo i4 = TaskInfo.create().
            id( TaskId.from( "123" ) ).
            name( "name" ).
            description( "test" ).
            state( TaskState.FINISHED ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( t ).
            build();
        final TaskInfo i5 = TaskInfo.create().
            id( TaskId.from( "123" ) ).
            name( "name" ).
            description( "test" ).
            state( TaskState.FINISHED ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( t ).
            build();

        assertEquals( true, i1.equals( i2 ) );
        assertEquals( false, i1.equals( i3 ) );
        assertEquals( false, i1.equals( "test" ) );
        assertTrue( i4.equals( i5 ) );
    }

    @Test
    public void testHashCode()
    {
        final Instant t = Instant.parse( "2017-10-01T09:00:00Z" );

        final TaskInfo i1 = TaskInfo.create().id( TaskId.from( "123" ) ).startTime( t ).build();
        final TaskInfo i2 = TaskInfo.create().id( TaskId.from( "123" ) ).startTime( t ).build();
        final TaskInfo i3 = TaskInfo.create().id( TaskId.from( "321" ) ).startTime( t ).build();

        final TaskInfo i4 = TaskInfo.create().
            id( TaskId.from( "123" ) ).
            name( "name" ).
            description( "test" ).
            state( TaskState.FINISHED ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( t ).
            build();
        final TaskInfo i5 = TaskInfo.create().
            id( TaskId.from( "123" ) ).
            name( "name" ).
            description( "test" ).
            state( TaskState.FINISHED ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( t ).
            build();

        assertEquals( i1.hashCode(), i2.hashCode() );
        assertNotEquals( i1.hashCode(), i3.hashCode() );
        assertEquals( i4.hashCode(), i5.hashCode() );
    }

    @Test
    public void testToString()
    {
        final TaskInfo i = TaskInfo.create().
            id( TaskId.from( "123" ) ).
            name( "name" ).
            description( "test" ).
            state( TaskState.FINISHED ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            build();
        assertEquals( "TaskInfo{id=123, name=name, description=test, state=FINISHED, " +
                          "progress=TaskProgress{current=0, total=0, info=}, application=com.enonic.myapp, " +
                          "user=user:store:me, startTime=2017-10-01T09:00:00Z}", i.toString() );
    }
}
