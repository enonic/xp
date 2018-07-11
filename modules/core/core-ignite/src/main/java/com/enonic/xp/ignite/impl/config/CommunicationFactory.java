package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;

class CommunicationFactory
{
    static TcpCommunicationSpi create( final IgniteSettings igniteSettings )
    {
        return new TcpCommunicationSpi().
            setMessageQueueLimit( igniteSettings.communication_message_queue_limit() );
    }
}
