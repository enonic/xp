package com.enonic.xp.admin.impl.rest.resource.issue;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.SimpleExecutor;

@Component
public class IssueMailSendExecutorImpl
    implements IssueMailSendExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( IssueMailSendExecutorImpl.class );

    private final SimpleExecutor simpleExecutor;

    public IssueMailSendExecutorImpl()
    {
        simpleExecutor = new SimpleExecutor( Executors::newCachedThreadPool, "issue-mail-sender-thread-%d",
                                             e -> LOG.error( "Message sending failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        simpleExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ), neverCommenced -> LOG.warn( "Not all messages were sent" ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        simpleExecutor.execute( command );
    }
}
