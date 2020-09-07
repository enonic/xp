package com.enonic.xp.core.impl.content;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.project.ProjectService;

@Component(immediate = true, configurationPid = "com.enonic.xp.content")
public class ParentProjectSyncActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( ParentProjectSyncActivator.class );

    private SimpleRecurringJobScheduler recurringJobScheduler;

    private ProjectService projectService;

    private ContentService contentService;

    private MediaInfoService mediaInfoService;

    @Activate
    public void initialize( final ContentConfig config )
    {
        this.recurringJobScheduler =
            new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "parent-project-synchronizer-thread" );

        final Duration delay = Duration.parse( config.content_sync_period() );
        if ( !delay.isZero() )
        {
            this.recurringJobScheduler.scheduleWithFixedDelay( ParentProjectSyncTask.create().
                contentService( this.contentService ).
                projectService( this.projectService ).
                mediaInfoService( this.mediaInfoService ).
                build(), Duration.ofMinutes( 0 ), delay, e -> LOG.warn( "Error while project sync.", e ), e -> LOG.error(
                "Error while project sync, no further attempts will be made.", e ) );
        }
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

    @Reference
    public void setMediaInfoService( final MediaInfoService mediaInfoService )
    {
        this.mediaInfoService = mediaInfoService;
    }

}
