package com.enonic.xp.impl.task.event;

import java.time.Instant;
import java.util.Map;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;

import static org.junit.Assert.*;

public class TaskEventsTest
{

    @Test
    public void submitted()
    {
        TaskInfo taskInfo = TaskInfo.create().
            id( TaskId.from( "task1" ) ).
            name( "name1" ).
            description( "Task1 description" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            progress( TaskProgress.create().current( 2 ).total( 42 ).info( "Processing" ).build() ).
            build();

        final Event event = TaskEvents.submitted( taskInfo );
        assertEquals( TaskEvents.TASK_SUBMITTED_EVENT, event.getType() );
        assertEquals( "task1", event.getValueAs( String.class, "id" ).get() );
        assertEquals( "name1", event.getValueAs( String.class, "name" ).get() );
        assertEquals( "Task1 description", event.getValueAs( String.class, "description" ).get() );
        assertEquals( "WAITING", event.getValueAs( String.class, "state" ).get() );
        assertEquals( "com.enonic.myapp", event.getValueAs( String.class, "application" ).get() );
        assertEquals( "user:store:me", event.getValueAs( String.class, "user" ).get() );
        assertEquals( "2017-10-01T09:00:00Z", event.getValueAs( String.class, "startTime" ).get() );

        Map<?, ?> progress = event.getValueAs( Map.class, "progress" ).get();
        assertEquals( 2, progress.get( "current" ) );
        assertEquals( 42, progress.get( "total" ) );
        assertEquals( "Processing", progress.get( "info" ) );
    }

    @Test
    public void updated()
    {
        TaskInfo taskInfo = TaskInfo.create().
            id( TaskId.from( "task1" ) ).
            name( "name1" ).
            description( "Task1 description" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            state( TaskState.RUNNING ).
            build();

        final Event event = TaskEvents.updated( taskInfo );
        assertEquals( TaskEvents.TASK_UPDATED_EVENT, event.getType() );
        assertEquals( "task1", event.getValueAs( String.class, "id" ).get() );
        assertEquals( "name1", event.getValueAs( String.class, "name" ).get() );
        assertEquals( "Task1 description", event.getValueAs( String.class, "description" ).get() );
        assertEquals( "RUNNING", event.getValueAs( String.class, "state" ).get() );
        assertEquals( "com.enonic.myapp", event.getValueAs( String.class, "application" ).get() );
        assertEquals( "user:store:me", event.getValueAs( String.class, "user" ).get() );
        assertEquals( "2017-10-01T09:00:00Z", event.getValueAs( String.class, "startTime" ).get() );
    }

    @Test
    public void finished()
    {
        TaskInfo taskInfo = TaskInfo.create().
            id( TaskId.from( "task1" ) ).
            name( "name1" ).
            description( "Task1 description" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            state( TaskState.FINISHED ).
            build();

        final Event event = TaskEvents.finished( taskInfo );
        assertEquals( TaskEvents.TASK_FINISHED_EVENT, event.getType() );
        assertEquals( "task1", event.getValueAs( String.class, "id" ).get() );
        assertEquals( "name1", event.getValueAs( String.class, "name" ).get() );
        assertEquals( "Task1 description", event.getValueAs( String.class, "description" ).get() );
        assertEquals( "FINISHED", event.getValueAs( String.class, "state" ).get() );
        assertEquals( "com.enonic.myapp", event.getValueAs( String.class, "application" ).get() );
        assertEquals( "user:store:me", event.getValueAs( String.class, "user" ).get() );
        assertEquals( "2017-10-01T09:00:00Z", event.getValueAs( String.class, "startTime" ).get() );
    }

    @Test
    public void failed()
    {
        TaskInfo taskInfo = TaskInfo.create().
            id( TaskId.from( "task1" ) ).
            name( "name1" ).
            description( "Task1 description" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            state( TaskState.FAILED ).
            build();

        final Event event = TaskEvents.failed( taskInfo );
        assertEquals( TaskEvents.TASK_FAILED_EVENT, event.getType() );
        assertEquals( "task1", event.getValueAs( String.class, "id" ).get() );
        assertEquals( "name1", event.getValueAs( String.class, "name" ).get() );
        assertEquals( "Task1 description", event.getValueAs( String.class, "description" ).get() );
        assertEquals( "FAILED", event.getValueAs( String.class, "state" ).get() );
        assertEquals( "com.enonic.myapp", event.getValueAs( String.class, "application" ).get() );
        assertEquals( "user:store:me", event.getValueAs( String.class, "user" ).get() );
        assertEquals( "2017-10-01T09:00:00Z", event.getValueAs( String.class, "startTime" ).get() );
    }

    @Test
    public void removed()
    {
        TaskInfo taskInfo = TaskInfo.create().
            id( TaskId.from( "task1" ) ).
            name( "name1" ).
            description( "Task1 description" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            state( TaskState.FINISHED ).
            build();

        final Event event = TaskEvents.removed( taskInfo );
        assertEquals( TaskEvents.TASK_REMOVED_EVENT, event.getType() );
        assertEquals( "task1", event.getValueAs( String.class, "id" ).get() );
        assertEquals( "name1", event.getValueAs( String.class, "name" ).get() );
        assertEquals( "Task1 description", event.getValueAs( String.class, "description" ).get() );
        assertEquals( "FINISHED", event.getValueAs( String.class, "state" ).get() );
        assertEquals( "com.enonic.myapp", event.getValueAs( String.class, "application" ).get() );
        assertEquals( "user:store:me", event.getValueAs( String.class, "user" ).get() );
        assertEquals( "2017-10-01T09:00:00Z", event.getValueAs( String.class, "startTime" ).get() );
    }


    @Test
    public void checkNull()
    {
        final Event event = TaskEvents.submitted( null );
        assertNull( event );
    }
}