package com.enonic.xp.admin.event.impl;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.Test;

public class WebsocketEventExecutorImplTest
{
    @Test
    void lifecycle()
    {
        final Phaser phaser = new Phaser( 2 );
        final WebsocketEventExecutorImpl executor = new WebsocketEventExecutorImpl();
        executor.execute( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        executor.deactivate();
    }
}
