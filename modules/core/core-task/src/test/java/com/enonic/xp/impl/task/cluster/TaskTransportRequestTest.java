package com.enonic.xp.impl.task.cluster;

import java.io.IOException;

import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.task.TaskId;

public class TaskTransportRequestTest
{
    @Test
    public void writeRead()
        throws IOException
    {
        final TaskTransportRequest oldRequest = new TaskTransportRequest( TaskTransportRequest.Type.BY_ID, TaskId.from( "taskId" ) );

        final BytesStreamOutput streamOutput = new BytesStreamOutput( 256 );
        oldRequest.writeTo( streamOutput );

        final TaskTransportRequest newRequest = new TaskTransportRequest();
        newRequest.readFrom( new BytesStreamInput( streamOutput.bytes() ) );

        Assert.assertEquals( oldRequest.getType(), newRequest.getType() );
        Assert.assertEquals( oldRequest.getTaskId(), newRequest.getTaskId() );
    }
}
