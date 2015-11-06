package com.enonic.xp.core.impl.event.cluster;

import java.io.IOException;

import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.event.Event2;
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
        Mockito.verify( this.transportService ).registerHandler( ClusterEventSender.ACTION, this.sendEventRequestHandler );
        this.sendEventRequestHandler.deactivate();
        Mockito.verify( this.transportService ).removeHandler( ClusterEventSender.ACTION );
    }

    @Test
    public void testMessageReceived()
        throws IOException
    {

        //Creates an event
        Event2 event = Event2.create( "eventType" ).
            timestamp( 123l ).
            distributed( true ).
            value( "key1", "value1" ).
            value( "key2", new Long( 1234l ) ).build();

        //Writes the event
        final BytesStreamOutput bytesStreamOutput = new BytesStreamOutput();
        final SendEventRequest sendEventRequestOut = new SendEventRequest( event );
        sendEventRequestOut.writeTo( bytesStreamOutput );

        //Reads the event
        final BytesStreamInput bytesStreamInput = new BytesStreamInput( bytesStreamOutput.bytes() );
        final SendEventRequest sendEventRequestIn = this.sendEventRequestHandler.newInstance();
        sendEventRequestIn.readFrom( bytesStreamInput );

        //Passes the event received to SendEventRequestHandler
        this.sendEventRequestHandler.messageReceived( sendEventRequestIn, Mockito.mock( TransportChannel.class ) );

        //Checks that the event was correctly published
        ArgumentCaptor<Event2> argumentCaptor = ArgumentCaptor.forClass( Event2.class );
        Mockito.verify( this.eventPublisher ).publish( argumentCaptor.capture() );
        final Event2 eventForwarded = argumentCaptor.getValue();
        Assert.assertEquals( eventForwarded.getType(), event.getType() );
        Assert.assertEquals( eventForwarded.getTimestamp(), event.getTimestamp() );
        Assert.assertEquals( eventForwarded.isDistributed(), false );
        Assert.assertEquals( eventForwarded.getData(), event.getData() );
    }

    @Test
    public void testExecutor()
    {
        Assert.assertEquals( ThreadPool.Names.MANAGEMENT, this.sendEventRequestHandler.executor() );
    }
}
