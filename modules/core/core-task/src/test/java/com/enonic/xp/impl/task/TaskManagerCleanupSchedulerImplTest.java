package com.enonic.xp.impl.task;

import java.time.Duration;
import java.util.concurrent.Phaser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerCleanupSchedulerImplTest
{
    @Test
    void lifecycle()
    {
        Phaser phaser = new Phaser( 2 );
        final TaskManagerCleanupSchedulerImpl taskManagerCleanupScheduler =
            new TaskManagerCleanupSchedulerImpl( Duration.ZERO, Duration.ofMillis( 1 ) );
        taskManagerCleanupScheduler.scheduleWithFixedDelay( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndAwaitAdvance();

        assertTrue( phaser.getPhase() >= 2, "Task should complete two or more times" );
        taskManagerCleanupScheduler.deactivate();
    }
}