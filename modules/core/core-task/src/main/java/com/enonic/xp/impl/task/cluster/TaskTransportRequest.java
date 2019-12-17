package com.enonic.xp.impl.task.cluster;

import java.io.IOException;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.transport.TransportRequest;

import com.enonic.xp.task.TaskId;

public final class TaskTransportRequest
    extends TransportRequest
{
    public enum Type
    {
        ALL,
        RUNNING,
        BY_ID
    }

    private Type type;

    private TaskId taskId;

    public TaskTransportRequest()
    {
    }

    public TaskTransportRequest( final Type type, final TaskId taskId )
    {
        this.type = type;
        this.taskId = taskId;
    }

    public Type getType()
    {
        return type;
    }

    public TaskId getTaskId()
    {
        return taskId;
    }

    public void readFrom( final StreamInput streamInput )
        throws IOException
    {
        type = Type.values()[streamInput.readInt()];
        if ( type == Type.BY_ID )
        {
            taskId = TaskId.from( streamInput.readString() );
        }
    }

    @Override
    public void writeTo( final StreamOutput streamOutput )
        throws IOException
    {
        streamOutput.writeInt( type.ordinal() );
        if ( type == Type.BY_ID )
        {
            streamOutput.writeString( taskId.toString() );
        }
    }
}
