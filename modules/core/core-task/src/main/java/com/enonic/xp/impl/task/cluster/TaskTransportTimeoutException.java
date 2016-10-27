package com.enonic.xp.impl.task.cluster;

public class TaskTransportTimeoutException
    extends RuntimeException
{

    public TaskTransportTimeoutException( final long timeoutTime )
    {
        super( "Task transport timeout exception after " + timeoutTime + "ms." );
    }
}
