package com.enonic.xp.impl.task.cluster;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;

public class TaskTransportResponseTest
{
    @Test
    public void writeRead()
        throws IOException
    {
        TaskInfo taskInfo1 = TaskInfo.create().
            id( TaskId.from( "task1" ) ).
            name( "name1" ).
            description( "Task1 on node1" ).
            progress( TaskProgress.EMPTY ).
            state( TaskState.WAITING ).
            build();
        TaskInfo taskInfo2 = TaskInfo.create().
            id( TaskId.from( "task2" ) ).
            name( "name2" ).
            description( "Task2 on node1" ).
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            progress( TaskProgress.EMPTY ).
            state( TaskState.FINISHED ).
            build();

        final TaskTransportResponse oldResponse = new TaskTransportResponse( Arrays.asList( taskInfo1, taskInfo2 ) );

        final BytesStreamOutput streamOutput = new BytesStreamOutput( 256 );
        oldResponse.writeTo( streamOutput );

        final TaskTransportResponse newResponse = new TaskTransportResponse();
        newResponse.readFrom( new BytesStreamInput( streamOutput.bytes() ) );

        Assert.assertEquals( oldResponse.getTaskInfos(), newResponse.getTaskInfos() );
    }
}
