package com.enonic.xp.core.impl.content;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.Test;

class ProjectContentSyncExecutorTest
{
    @Test
    void lifecycle()
    {
        Phaser phaser = new Phaser( 2 );
        final ProjectContentSyncExecutor eventPublisherExecutorImpl = new ProjectContentSyncExecutor();
        eventPublisherExecutorImpl.execute( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        eventPublisherExecutorImpl.deactivate();
    }
}