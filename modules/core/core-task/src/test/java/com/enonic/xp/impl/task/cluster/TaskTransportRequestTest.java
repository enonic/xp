package com.enonic.xp.impl.task.cluster;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.elasticsearch.common.io.stream.ByteBufferStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.junit.jupiter.api.Test;

import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        final StreamInput bytesStreamInput = new ByteBufferStreamInput( ByteBuffer.wrap( streamOutput.bytes().array() ) );
        newRequest.readFrom( bytesStreamInput );

        assertEquals( oldRequest.getType(), newRequest.getType() );
        assertEquals( oldRequest.getTaskId(), newRequest.getTaskId() );
    }
}
