package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class CommunicationFactoryTest
{

    private IgniteSettings igniteSettings;

    @Before
    public void setUp()
        throws Exception
    {
        this.igniteSettings = Mockito.mock( IgniteSettings.class );
    }

    @Test
    public void message_queue_limit_setting()
        throws Exception
    {
        Mockito.when( this.igniteSettings.communication_message_queue_limit() ).thenReturn( 42 );

        final TcpCommunicationSpi communicationSpi = CommunicationFactory.create( this.igniteSettings );

        final long messageQueueLimit = communicationSpi.getMessageQueueLimit();

        assertEquals( 42, messageQueueLimit );
    }
}