package com.enonic.xp.core.impl.event.cluster;

import java.io.IOException;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.transport.TransportRequest;

import com.enonic.xp.event.Event;

// See https://github.com/enonic/cms/blob/master/cms-ee/cms-ee-core/src/main/java/com/enonic/cms/ee/cluster/SendClusterEventRequest.java
public final class SendEventRequest
    extends TransportRequest
    implements Streamable
{
    private Event event;

    public SendEventRequest()
    {
        this( null );
    }

    public SendEventRequest( final Event event )
    {
        this.event = event;
    }

    public Event getEvent()
    {
        return this.event;
    }

    @Override
    public void readFrom( final StreamInput in )
        throws IOException
    {
    }

    @Override
    public void writeTo( final StreamOutput out )
        throws IOException
    {
    }
}
