package com.enonic.xp.core.impl.content.event;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.core.impl.content.serializer.PublishInfoSerializer;
import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.repository.RepositoryId;

@Component
public class ContentEventProducerImpl
    implements ContentEventProducer
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentEventProducer.class );

    private final EventPublisher eventPublisher;

    private final SimpleRecurringJobScheduler jobScheduler;

    private final NodeService nodeService;

    private final PriorityBlockingQueue<ContentEvent> eventQueue;

    private final PublishInfoSerializer publishInfoSerializer;

    @Activate
    public ContentEventProducerImpl( @Reference final EventPublisher eventPublisher, @Reference final NodeService nodeService )
    {
        this.eventPublisher = eventPublisher;
        this.nodeService = nodeService;
        this.eventQueue = new PriorityBlockingQueue<>( 11, Comparator.comparing( ContentEvent::getTime ) );
        this.publishInfoSerializer = new PublishInfoSerializer();

        this.jobScheduler = new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "content-publisher-thread" );
    }

    @Activate
//    @Modified
    public void activate( final ContentConfig config )
    {
        jobScheduler.scheduleWithFixedDelay( this::run, Duration.ofMinutes( 0 ), Duration.parse( config.event_period() ),
                                             e -> LOG.debug( "Error error while sending Content Events", e ),
                                             e -> LOG.error( "Error error while sending Content Events, no further attempts will be made",
                                                             e ) );
    }


    private void run()
    {
        final Instant now = Instant.now();
        EventBatch batch = null;

        while ( !eventQueue.isEmpty() )
        {

            final ContentEvent event = eventQueue.peek();

            if ( event.isValid() )
            {
                if ( event.timeToRun( now ).isNegative() )
                {
                    if ( batch == null )
                    {
                        batch = new EventBatch( event );
                    }
                    else
                    {
                        if ( !batch.addEvent( event ) )
                        {
                            runEvents( batch );
                            batch = new EventBatch( event );
                        }
                    }
                    eventQueue.remove();
                }
                else
                {
                    break;
                }
            }
            else
            {
                eventQueue.remove();
            }
        }

        if ( batch != null && !batch.isEmpty() )
        {
            runEvents( batch );
        }
    }

    private void runEvents( final EventBatch batch )
    {
        switch ( batch.type )
        {
            case ONLINE:
                this.eventPublisher.publish( ContentEvents.online( batch.getEvents() ) );
                break;
            case OFFLINE:
                this.eventPublisher.publish( ContentEvents.offline( batch.getEvents() ) );
                break;
            default:
                throw new IllegalStateException( "invalid content event type: " + batch.getType() );
        }
    }

    @Deactivate
    void deactivate()
    {
        jobScheduler.shutdownNow();
    }

    @Override
    public void published( final PushNodesResult result )
    {
        final Set<NodeId> invalidatedOnline = invalidate( result.getSuccessful().getKeys(), ContentEventType.ONLINE );
        invalidate( result.getSuccessful().getKeys(), ContentEventType.OFFLINE );

        createOfflineMovedEvents( result.getSuccessfulEntries(),
                                  invalidatedOnline );  // send OFFLINE for published and moved, or republished to the future

        createPublishedEvents( result.getSuccessful() );
    }

    @Override
    public void unpublished( final NodeBranchEntries entries )
    {
        invalidate( entries.getKeys(), ContentEventType.ONLINE );
        invalidate( entries.getKeys(), ContentEventType.OFFLINE );

        createUnpublishedEvents( entries );
    }

    private Set<NodeId> invalidate( final Set<NodeId> toInvalidate, final ContentEventType type )
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();

        return eventQueue.stream()
            .filter( event -> type.equals( event.getType() ) && toInvalidate.contains( event.getNodeId() ) &&
                repositoryId.equals( event.getRepositoryId() ) && event.isValid() )
            .map( ContentEvent::invalidate )
            .collect( Collectors.toSet() );
    }

    private void createPublishedEvents( final NodeBranchEntries result )
    {
        final List<ContentEvent> eventsFrom = createPublishedEvents( result, ContentEventType.ONLINE );
        final List<ContentEvent> eventsTo = createPublishedEvents( result, ContentEventType.OFFLINE );

        eventQueue.addAll( eventsFrom );
        eventQueue.addAll( eventsTo );
    }

    private void createUnpublishedEvents( final NodeBranchEntries entries )
    {
        final Instant time = Instant.now();

        final List<ContentEvent> events = entries.stream()
            .map( nodeBranchEntry -> ContentEvent.create()
                .nodeId( nodeBranchEntry.getNodeId() )
                .nodePath( nodeBranchEntry.getNodePath() )
                .nodeVersionId( nodeBranchEntry.getVersionId() )
                .time( time )
                .type( ContentEventType.OFFLINE )
                .build() )
            .collect( Collectors.toList() );

        eventQueue.addAll( events );
    }

    private void createOfflineMovedEvents( final List<PushNodeEntry> result, final Set<NodeId> invalidated )
    {
        final Instant now = Instant.now();

        final List<ContentEvent> events = result.stream()
            .filter( e -> e.getCurrentTargetPath() != null && !e.getCurrentTargetPath().equals( e.getNodeBranchEntry().getNodePath() ) )
            .filter( e -> !invalidated.contains( e.getNodeBranchEntry().getNodeId() ) )
            .map( pushNodeEntry -> ContentEvent.create()
                .type( ContentEventType.OFFLINE )
                .nodeId( pushNodeEntry.getNodeBranchEntry().getNodeId() )
                .nodePath( pushNodeEntry.getCurrentTargetPath() )
                .nodeVersionId( pushNodeEntry.getNodeBranchEntry().getVersionId() )
                .time( now )
                .build() )
            .collect( Collectors.toList() );

        eventQueue.addAll( events );
    }

    private List<ContentEvent> createPublishedEvents( final NodeBranchEntries result, final ContentEventType type )
    {
        return result.stream().map( nodeBranchEntry -> {
            final NodeVersion nodeVersion = nodeService.getByNodeVersionKey( nodeBranchEntry.getNodeVersionKey() );
            if ( nodeVersion == null )
            {
                return null;
            }
            final ContentPublishInfo contentPublishInfo = publishInfoSerializer.serialize( nodeVersion.getData().getRoot() );

            if ( contentPublishInfo == null )
            {
                return null;
            }

            switch ( type )
            {
                case ONLINE:

                    return contentPublishInfo.getFrom() != null ? ContentEvent.create()
                        .type( type )
                        .nodeId( nodeBranchEntry.getNodeId() )
                        .nodePath( nodeBranchEntry.getNodePath() )
                        .nodeVersionId( nodeBranchEntry.getVersionId() )
                        .time( contentPublishInfo.getFrom() )
                        .build() : null;
                case OFFLINE:
                    return contentPublishInfo.getTo() != null ? ContentEvent.create()
                        .type( type )
                        .nodeId( nodeBranchEntry.getNodeId() )
                        .nodePath( nodeBranchEntry.getNodePath() )
                        .nodeVersionId( nodeBranchEntry.getVersionId() )
                        .time( contentPublishInfo.getTo() )
                        .build() : null;
                default:
                    throw new IllegalStateException( "unknown content event type: " + type );
            }

        } ).filter( Objects::nonNull ).collect( Collectors.toList() );
    }


    private static final class EventBatch
    {
        private final List<ContentEvent> events;

        private final Instant time;

        private final ContentEventType type;

        private final RepositoryId repositoryId;

        public EventBatch( final ContentEvent event )
        {
            this.time = event.getTime();
            this.type = event.getType();
            this.repositoryId = event.getRepositoryId();

            this.events = new LinkedList<>( List.of( event ) );
        }

        public boolean addEvent( final ContentEvent event )
        {
            if ( this.time.equals( event.getTime() ) && this.type.equals( event.getType() ) &&
                this.repositoryId.equals( event.getRepositoryId() ) )
            {
                this.events.add( event );
                return true;
            }

            return false;
        }

        public boolean isEmpty()
        {
            return events.isEmpty();
        }

        public List<ContentEvent> getEvents()
        {
            return events;
        }

        public ContentEventType getType()
        {
            return type;
        }
    }

}
