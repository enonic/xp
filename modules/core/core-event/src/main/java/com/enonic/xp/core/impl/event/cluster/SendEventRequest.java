package com.enonic.xp.core.impl.event.cluster;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.transport.TransportRequest;

import com.enonic.xp.event.Event;

public final class SendEventRequest
    extends TransportRequest
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

    public void readFrom( final StreamInput streamInput )
        throws IOException
    {
        final String type = streamInput.readString();
        final long timestamp = streamInput.readLong();
        final boolean distributed = streamInput.readBoolean();
        final Map<String, Object> data = streamInput.readMap();

        final Event.Builder eventBuilder = Event.create( type ).
            timestamp( timestamp ).
            distributed( distributed );
        for ( Map.Entry<String, Object> dataEntry : data.entrySet() )
        {
            eventBuilder.value( dataEntry.getKey(), dataEntry.getValue() );
        }
        this.event = eventBuilder.build();
    }

    @Override
    public void writeTo( final StreamOutput streamOutput )
        throws IOException
    {
        if ( event != null )
        {
            streamOutput.writeString( event.getType() );
            streamOutput.writeLong( event.getTimestamp() );
            streamOutput.writeBoolean( event.isDistributed() );
            streamOutput.writeMap( event.getData() );
        }
    }
}
