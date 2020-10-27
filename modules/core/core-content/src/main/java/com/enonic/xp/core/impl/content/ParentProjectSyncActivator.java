package com.enonic.xp.core.impl.content;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;
import com.enonic.xp.project.ProjectService;

@Component(immediate = true, configurationPid = "com.enonic.xp.content")
public final class ParentProjectSyncActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( ParentProjectSyncActivator.class );

    private SimpleRecurringJobScheduler recurringJobScheduler;

    @Activate
    public ParentProjectSyncActivator( final ContentConfig config, @Reference final ProjectService projectService,
                                       @Reference final ContentSynchronizer contentSynchronizer )
    {

        this.recurringJobScheduler =
            new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "parent-project-synchronizer-thread" );

        final Duration delay = Duration.parse( config.content_sync_period() );
        if ( !delay.isZero() )
        {
            this.recurringJobScheduler.scheduleWithFixedDelay( ParentProjectSyncTask.create().
                projectService( projectService ).
                contentSynchronizer( contentSynchronizer ).
                build(), Duration.ofMinutes( 0 ), delay, e -> LOG.warn( "Error while project sync.", e ), e -> LOG.error(
                "Error while project sync, no further attempts will be made.", e ) );
        }
    }

    @Deactivate
    public void deactivate()
    {
        this.recurringJobScheduler.shutdownNow();
    }
}
