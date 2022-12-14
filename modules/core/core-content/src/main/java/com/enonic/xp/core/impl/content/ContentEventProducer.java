package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.Content;
import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;
import com.enonic.xp.event.EventPublisher;

@Component(service = ContentEventProducer.class)
public class ContentEventProducer
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentEventProducer.class );

    private final EventPublisher eventPublisher;

    private final SimpleRecurringJobScheduler jobScheduler;


    @Activate
    public ContentEventProducer( @Reference final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
        this.jobScheduler = new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "content-publisher-thread" );
    }

    @Activate
    public void activate()
    {
        //jobScheduler.scheduleWithFixedDelay( , Duration.ofSeconds( 1 ), Duration.ofSeconds( 1 ),
        //                                       e -> LOG.debug( "Error error while sending Content Events", e ),
        //                                      e -> LOG.error( "Error error while sending Content Events, no further attempts will be made", e ) );
    }

    @Deactivate
    void deactivate()
    {
        jobScheduler.shutdownNow();
    }

    public void put( final List<Content> online, final List<Content> offline )
    {
        this.eventPublisher.publish( ContentEvents.offline( offline ) );
        this.eventPublisher.publish( ContentEvents.online( online ) );
    }

    public void put( final List<Content> offline )
    {
        this.eventPublisher.publish( ContentEvents.offline( offline ) );
    }

}
