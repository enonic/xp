package com.enonic.xp.impl.task;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.Test;

class TaskManagerExecutorImplTest
{
    @Test
    void lifecycle()
    {
        Phaser phaser = new Phaser( 2 );
        final TaskManagerExecutorImpl taskManagerExecutor = new TaskManagerExecutorImpl();
        taskManagerExecutor.execute( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        taskManagerExecutor.deactivate();
    }
}