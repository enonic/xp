package com.enonic.xp.core.impl.event.cluster;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.elasticsearch.common.io.stream.ByteBufferStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.junit.jupiter.api.Test;

import com.enonic.xp.event.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendEventRequestTest
{

    @Test
    public void testWriteRead()
        throws IOException
    {
        Event event = Event.create( "eventType" ).
            timestamp( 123L ).
            distributed( true ).
            value( "key1", "value1" ).
            value( "key2", 1234L ).build();

        //Writes the event
        final BytesStreamOutput bytesStreamOutput = new BytesStreamOutput();
        final SendEventRequest sendEventRequestOut = new SendEventRequest( event );
        sendEventRequestOut.writeTo( bytesStreamOutput );

        //Reads the event
        final StreamInput bytesStreamInput = new ByteBufferStreamInput( ByteBuffer.wrap( bytesStreamOutput.bytes().array() ) );
        final SendEventRequest sendEventRequestIn = new SendEventRequest();
        sendEventRequestIn.readFrom( bytesStreamInput );

        assertEquals( event, sendEventRequestIn.getEvent() );
    }
}
