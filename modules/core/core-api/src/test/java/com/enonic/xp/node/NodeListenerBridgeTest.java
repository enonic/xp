package com.enonic.xp.node;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeListenerBridgeTest
{
    @Test
    void apply_permissions_listener_of_the_old_name_alone_still_hears_the_total()
    {
        final AtomicInteger heard = new AtomicInteger();

        final ApplyNodePermissionsListener listener = new ApplyNodePermissionsListener()
        {
            @Override
            public void permissionsApplied( final int count )
            {
            }

            @Override
            public void notEnoughRights( final int count )
            {
            }

            @Override
            @SuppressWarnings("deprecation")
            public void setTotal( final int count )
            {
                heard.set( count );
            }
        };

        listener.resolved( 7 );

        assertEquals( 7, heard.get() );
    }
}
