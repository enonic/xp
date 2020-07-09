package com.enonic.xp.core.impl.project;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;
import com.enonic.xp.project.ProjectService;

@Component(immediate = true)
public class ParentProjectSyncActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( ParentProjectSyncActivator.class );

    private SimpleRecurringJobScheduler recurringJobScheduler;

    private ProjectService projectService;

    private ContentService contentService;

    @Activate
    public void initialize()
    {
        this.recurringJobScheduler =
            new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "parent-project-synchronizer-thread" );

        this.recurringJobScheduler.scheduleWithFixedDelay( ParentProjectSyncTask.create().
                                                               contentService( this.contentService ).
                                                               projectService( this.projectService ).
                                                               build(), Duration.ofMinutes( 0 ), Duration.ofMinutes( 1 ), e -> LOG.warn( "Error while project sync.", e ),
                                                           e -> LOG.error( "Error while project sync, no further attempts will be made.",
                                                                           e ) );
    }

    @Reference
    public void setProjectService( final ProjectService projectService )
    {
        this.projectService = projectService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

}
