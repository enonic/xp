package com.enonic.xp.content;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The deprecated total forwards from {@code resolved} and not the other way round: producers call {@code resolved}, so a listener that
 * implements only the old name would stop hearing anything the moment that direction were inverted.
 */
class ContentListenerBridgeTest
{
    @Test
    void push_listener_of_the_old_name_alone_still_hears_the_total()
    {
        final AtomicInteger heard = new AtomicInteger();

        final PushContentListener listener = new PushContentListener()
        {
            @Override
            public void contentPushed( final int count )
            {
            }

            @Override
            @SuppressWarnings("deprecation")
            public void contentResolved( final int count )
            {
                heard.set( count );
            }
        };

        listener.resolved( 7 );

        assertEquals( 7, heard.get() );
    }

    @Test
    void apply_permissions_listener_of_the_old_name_alone_still_hears_the_total()
    {
        final AtomicInteger heard = new AtomicInteger();

        final ApplyPermissionsListener listener = new ApplyPermissionsListener()
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
