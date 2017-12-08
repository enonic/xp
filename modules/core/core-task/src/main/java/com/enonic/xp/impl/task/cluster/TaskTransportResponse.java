package com.enonic.xp.impl.task.cluster;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.transport.TransportResponse;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;

public final class TaskTransportResponse
    extends TransportResponse
{
    private List<TaskInfo> taskInfos;

    public TaskTransportResponse()
    {
        this( null );
    }

    public TaskTransportResponse( final List<TaskInfo> taskInfos )
    {
        this.taskInfos = taskInfos;
    }

    public List<TaskInfo> getTaskInfos()
    {
        return taskInfos;
    }

    @Override
    public void readFrom( final StreamInput streamInput )
        throws IOException
    {
        final ImmutableList.Builder<TaskInfo> taskInfos = ImmutableList.builder();
        final int taskInfoCount = streamInput.readInt();
        for ( int i = 0; i < taskInfoCount; i++ )
        {
            TaskInfo taskInfo = readTaskInfoFrom( streamInput );
            taskInfos.add( taskInfo );
        }

        this.taskInfos = taskInfos.build();
    }

    private TaskInfo readTaskInfoFrom( final StreamInput streamInput )
        throws IOException
    {
        return TaskInfo.create().
            id( TaskId.from( streamInput.readString() ) ).
            name( streamInput.readString() ).
            description( streamInput.readString() ).
            state( TaskState.values()[streamInput.readInt()] ).
            application( ApplicationKey.from( streamInput.readString() ) ).
            user( PrincipalKey.from( streamInput.readString() ) ).
            startTime( Instant.parse( streamInput.readString() ) ).
            progress( readTaskProgressFrom( streamInput ) ).
            build();
    }

    private TaskProgress readTaskProgressFrom( final StreamInput streamInput )
        throws IOException
    {
        return TaskProgress.create().
            current( streamInput.readInt() ).
            total( streamInput.readInt() ).
            info( streamInput.readString() ).
            build();
    }

    @Override
    public void writeTo( final StreamOutput streamOutput )
        throws IOException
    {
        if ( taskInfos != null )
        {
            streamOutput.writeInt( taskInfos.size() );
            for ( TaskInfo taskInfo : taskInfos )
            {
                writeTo( streamOutput, taskInfo );
            }
        }
    }

    private void writeTo( final StreamOutput streamOutput, final TaskInfo taskInfo )
        throws IOException
    {
        streamOutput.writeString( taskInfo.getId().toString() );
        streamOutput.writeString( taskInfo.getName() );
        streamOutput.writeString( taskInfo.getDescription() );
        streamOutput.writeInt( taskInfo.getState().ordinal() );
        streamOutput.writeString( taskInfo.getApplication().toString() );
        streamOutput.writeString( taskInfo.getUser().toString() );
        streamOutput.writeString( taskInfo.getStartTime().toString() );
        writeTo( streamOutput, taskInfo.getProgress() );
    }

    private void writeTo( final StreamOutput streamOutput, final TaskProgress taskProgress )
        throws IOException
    {
        streamOutput.writeInt( taskProgress.getCurrent() );
        streamOutput.writeInt( taskProgress.getTotal() );
        streamOutput.writeString( taskProgress.getInfo() );
    }
}
