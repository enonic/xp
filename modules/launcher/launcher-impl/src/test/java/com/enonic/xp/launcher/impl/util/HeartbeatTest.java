package com.enonic.xp.launcher.impl.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HeartbeatTest
{
    @Test
    void message()
    {
        final String message = new Heartbeat( "8.0.0" ).buildMessage();
        assertTrue( message.matches(
            "XP Heartbeat seq=1 uptime=\\d+d\\d+h\\d+m\\d+s version=8\\.0\\.0 heap=\\d+/-?\\d+M gc=\\d+/\\d+ms threads=\\d+" ), message );
    }

    @Test
    void seq_increments()
    {
        final Heartbeat heartbeat = new Heartbeat( "8.0.0" );
        assertTrue( heartbeat.buildMessage().startsWith( "XP Heartbeat seq=1 " ) );
        assertTrue( heartbeat.buildMessage().startsWith( "XP Heartbeat seq=2 " ) );
    }
}
