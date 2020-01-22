package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.Test;

class IssueMailSendExecutorImplTest
{
    @Test
    void lifecycle()
    {
        final Phaser phaser = new Phaser( 2 );
        final IssueMailSendExecutorImpl issueMailSendExecutor = new IssueMailSendExecutorImpl();
        issueMailSendExecutor.execute( phaser::arriveAndAwaitAdvance );

        phaser.arriveAndAwaitAdvance();
        issueMailSendExecutor.deactivate();
    }
}