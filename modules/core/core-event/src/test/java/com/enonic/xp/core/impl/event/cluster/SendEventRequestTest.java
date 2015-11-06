package com.enonic.xp.core.impl.event.cluster;

import java.io.IOException;

import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.event.Event2;

public class SendEventRequestTest
{

    @Test
    public void testWriteRead()
        throws IOException
    {
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
        final SendEventRequest sendEventRequestIn = new SendEventRequest();
        sendEventRequestIn.readFrom( bytesStreamInput );

        Assert.assertTrue( event.equals( sendEventRequestIn.getEvent() ) );
    }
}
