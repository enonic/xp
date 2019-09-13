package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class CommunicationFactoryTest
{

    private IgniteSettings igniteSettings;

    @BeforeEach
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
