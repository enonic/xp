package com.enonic.xp.impl.task.cluster;

import java.util.List;

import org.elasticsearch.transport.TransportException;
import org.elasticsearch.transport.TransportResponseHandler;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.util.Exceptions;

public class TaskTransportResponseHandler
    implements TransportResponseHandler<TaskTransportResponse>
{
    private final ImmutableList.Builder<TaskInfo> taskInfos = ImmutableList.builder();

    private int awaitingResponseCount;

    private TransportException transportException;

    public TaskTransportResponseHandler( final int responseCount )
    {
        Preconditions.checkArgument( responseCount > 0, "responseCount must be greater than 0" );
        this.awaitingResponseCount = responseCount;
    }


    @Override
    public TaskTransportResponse newInstance()
    {
        return new TaskTransportResponse();
    }

    @Override
    public synchronized void handleResponse( final TaskTransportResponse response )
    {
        taskInfos.addAll( response.getTaskInfos() );
        awaitingResponseCount--;
        this.notifyAll();
    }

    @Override
    public synchronized void handleException( final TransportException e )
    {
        transportException = e;
        this.notifyAll();
    }

    @Override
    public String executor()
    {
        return null;
    }

    public synchronized List<TaskInfo> getTaskInfos()
    {
        while ( transportException == null && awaitingResponseCount > 0 )
        {
            try
            {
                this.wait();
            }
            catch ( InterruptedException e )
            {
                throw Exceptions.unchecked( e );
            }
        }

        if ( transportException != null )
        {
            throw transportException;
        }
        return taskInfos.build();


    }
}
