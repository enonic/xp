package com.enonic.xp.core.impl.content;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.Test;

class ContentAuditLogExecutorImplTest
{
    @Test
    void lifecycle()
    {
        Phaser phaser = new Phaser( 2 );
        final ContentAuditLogExecutor taskManagerExecutor = new ContentAuditLogExecutor();
        taskManagerExecutor.execute( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        taskManagerExecutor.deactivate();
    }
}
