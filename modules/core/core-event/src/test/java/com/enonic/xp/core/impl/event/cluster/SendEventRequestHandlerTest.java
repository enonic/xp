package com.enonic.xp.core.impl.event.cluster;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.elasticsearch.common.io.stream.ByteBufferStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;

public class SendEventRequestHandlerTest
{
    private SendEventRequestHandler sendEventRequestHandler;

    private EventPublisher eventPublisher;

    private TransportService transportService;

    @Before
    public void setUp()
    {
        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.transportService = Mockito.mock( TransportService.class );

        this.sendEventRequestHandler = new SendEventRequestHandler();
        this.sendEventRequestHandler.setEventPublisher( this.eventPublisher );
        this.sendEventRequestHandler.setTransportService( this.transportService );
    }

    @Test
    public void testActivationDeactivation()
        throws IOException
    {
        this.sendEventRequestHandler.activate();
        Mockito.verify( this.transportService ).registerRequestHandler( ClusterEventSender.ACTION, SendEventRequest.class,
                                                                        ThreadPool.Names.MANAGEMENT, this.sendEventRequestHandler );
        this.sendEventRequestHandler.deactivate();
        Mockito.verify( this.transportService ).removeHandler( ClusterEventSender.ACTION );
    }

    @Test
    public void testMessageReceived()
        throws IOException
    {

        //Creates an event
        Event event = Event.create( "eventType" ).
            timestamp( 123l ).
            distributed( true ).
            value( "key1", "value1" ).
            value( "key2", new Long( 1234l ) ).build();

        //Writes the event
        final BytesStreamOutput bytesStreamOutput = new BytesStreamOutput();
        final SendEventRequest sendEventRequestOut = new SendEventRequest( event );
        sendEventRequestOut.writeTo( bytesStreamOutput );

        //Reads the event
        final StreamInput bytesStreamInput = new ByteBufferStreamInput( ByteBuffer.wrap( bytesStreamOutput.bytes().array() ) );
        final SendEventRequest sendEventRequestIn = new SendEventRequest();
        sendEventRequestIn.readFrom( bytesStreamInput );

        //Passes the event received to SendEventRequestHandler
        this.sendEventRequestHandler.messageReceived( sendEventRequestIn, Mockito.mock( TransportChannel.class ) );

        //Checks that the event was correctly published
        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );
        Mockito.verify( this.eventPublisher ).publish( argumentCaptor.capture() );
        final Event eventForwarded = argumentCaptor.getValue();
        Assert.assertEquals( eventForwarded.getType(), event.getType() );
        Assert.assertEquals( eventForwarded.getTimestamp(), event.getTimestamp() );
        Assert.assertEquals( eventForwarded.isDistributed(), false );
        Assert.assertEquals( eventForwarded.getData(), event.getData() );
    }
}
