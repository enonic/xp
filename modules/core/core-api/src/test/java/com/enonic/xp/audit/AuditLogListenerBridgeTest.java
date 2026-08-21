package com.enonic.xp.audit;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditLogListenerBridgeTest
{
    @Test
    void clean_up_listener_of_the_old_name_alone_still_hears_every_record()
    {
        final AtomicInteger heard = new AtomicInteger();

        final CleanUpAuditLogListener listener = new CleanUpAuditLogListener()
        {
            @Override
            @SuppressWarnings("deprecation")
            public void processed()
            {
                heard.incrementAndGet();
            }
        };

        listener.recordsDeleted( 3 );

        assertEquals( 3, heard.get() );
    }
}
