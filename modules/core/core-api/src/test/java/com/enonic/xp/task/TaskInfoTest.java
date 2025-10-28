package com.enonic.xp.task;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.support.SerializableUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskInfoTest
{
    @Test
    void testAccessors()
    {
        final TaskId id = TaskId.from( "123" );
        final TaskProgress progress = TaskProgress.EMPTY;

        final TaskInfo info = TaskInfo.create()
            .id( id )
            .name( "name" )
            .description( "test" )
            .state( TaskState.FINISHED )
            .application( ApplicationKey.from( "com.enonic.myapp" ) )
            .user( PrincipalKey.from( "user:store:me" ) )
            .startTime( Instant.parse( "2017-10-01T09:00:00Z" ) )
            .node( ClusterNodeId.from( "node1" ) )
            .progress( progress )
            .build();

        assertEquals( id, info.getId() );
        assertEquals( "name", info.getName() );
        assertEquals( "test", info.getDescription() );
        assertEquals( TaskState.FINISHED, info.getState() );
        assertEquals( progress, info.getProgress() );
        assertEquals( ApplicationKey.from( "com.enonic.myapp" ), info.getApplication() );
        assertEquals( PrincipalKey.from( "user:store:me" ), info.getUser() );
        assertEquals( Instant.parse( "2017-10-01T09:00:00Z" ), info.getStartTime() );
        assertEquals( ClusterNodeId.from( "node1" ), info.getNode() );
    }

    @Test
    void testState()
    {
        final TaskInfo info1 = TaskInfo.create()
            .id( TaskId.from( "123" ) )
            .application( ApplicationKey.BASE )
            .name( "some-name" )
            .startTime( Instant.now() )
            .state( TaskState.RUNNING )
            .build();

        assertEquals( TaskState.RUNNING, info1.getState() );
        assertTrue( info1.isRunning() );
        assertFalse( info1.isDone() );

        final TaskInfo info2 = info1.copy().state( TaskState.FINISHED ).build();

        assertEquals( TaskState.FINISHED, info2.getState() );
        assertFalse( info2.isRunning() );
        assertTrue( info2.isDone() );
    }

    @Test
    void testCopy()
    {
        final TaskInfo i1 = TaskInfo.create()
            .id( TaskId.from( "123" ) )
            .application( ApplicationKey.BASE )
            .name( "some-name" )
            .startTime( Instant.now() )
            .build();
        final TaskInfo i2 = i1.copy().description( "test" ).build();

        assertNotEquals( i1, i2 );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( TaskInfo.class ).verify();
    }

    @Test
    void serializable()
    {
        final TaskInfo info = TaskInfo.create()
            .id( TaskId.from( "123" ) )
            .application( ApplicationKey.BASE )
            .name( "some-name" )
            .startTime( Instant.now() )
            .state( TaskState.RUNNING )
            .build();
        final byte[] serializedObject = SerializableUtils.serialize( info );
        final TaskInfo deserializedObject = (TaskInfo) SerializableUtils.deserialize( serializedObject );
        assertEquals( info, deserializedObject );
    }

    @Test
    void testToString()
    {
        final TaskInfo i = TaskInfo.create()
            .id( TaskId.from( "123" ) )
            .name( "name" )
            .description( "test" )
            .state( TaskState.FINISHED )
            .application( ApplicationKey.from( "com.enonic.myapp" ) )
            .user( PrincipalKey.from( "user:store:me" ) )
            .startTime( Instant.parse( "2017-10-01T09:00:00Z" ) )
            .node( ClusterNodeId.from( "node1" ) )
            .build();
        assertEquals( "TaskInfo{id=123, name=name, description=test, state=FINISHED, " +
                          "progress=TaskProgress{current=0, total=0, info=}, application=com.enonic.myapp, " +
                          "user=user:store:me, startTime=2017-10-01T09:00:00Z, node=node1}", i.toString() );
    }
}
