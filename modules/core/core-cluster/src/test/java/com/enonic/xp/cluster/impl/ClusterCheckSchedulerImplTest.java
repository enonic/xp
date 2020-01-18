package com.enonic.xp.cluster.impl;

import java.time.Duration;
import java.util.concurrent.Phaser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClusterCheckSchedulerImplTest
{
    private ClusterCheckSchedulerImpl clusterCheckScheduler;

    private final Duration checkInterval = Duration.ofMillis( 1 );

    @BeforeEach
    void setUp()
    {
        clusterCheckScheduler = new ClusterCheckSchedulerImpl( Duration.ZERO, checkInterval );
    }

    @AfterEach
    void tearDown()
    {
        clusterCheckScheduler.deactivate();
    }

    @Test
    void lifecycle()
    {
        Phaser phaser = new Phaser( 2 );
        final ClusterCheckSchedulerImpl taskManagerCleanupScheduler =
            new ClusterCheckSchedulerImpl( Duration.ZERO, Duration.ofMillis( 1 ) );
        taskManagerCleanupScheduler.scheduleWithFixedDelay( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndAwaitAdvance();

        assertTrue( phaser.getPhase() >= 2, "Task should complete two or more times" );
        taskManagerCleanupScheduler.deactivate();
    }
}