package com.enonic.xp.impl.scheduler;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.Test;

class ScheduleAuditLogExecutorImplTest
{
    @Test
    void lifecycle()
    {
        final Phaser phaser = new Phaser( 2 );
        final ScheduleAuditLogExecutorImpl taskManagerExecutor = new ScheduleAuditLogExecutorImpl();
        taskManagerExecutor.execute( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        taskManagerExecutor.deactivate();
    }
}
