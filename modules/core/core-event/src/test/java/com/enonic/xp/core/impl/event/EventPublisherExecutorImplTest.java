package com.enonic.xp.core.impl.event;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.Test;

class EventPublisherExecutorImplTest
{
    @Test
    void lifecycle()
    {
        Phaser phaser = new Phaser( 2 );
        final EventPublisherExecutorImpl eventPublisherExecutorImpl = new EventPublisherExecutorImpl();
        eventPublisherExecutorImpl.execute( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        eventPublisherExecutorImpl.deactivate();
    }
}