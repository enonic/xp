package com.enonic.xp.core.impl.hazelcast.status.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class HazelcastObjectsReporter
    implements StatusReporter
{
    private static final Logger LOG = LoggerFactory.getLogger( HazelcastObjectsReporter.class );

    private final HazelcastInstance hazelcastInstance;

    @Activate
    public HazelcastObjectsReporter( @Reference final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public String getName()
    {
        return "hazelcast.objects";
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    private JsonNode getReport()
    {
        final Collection<DistributedObject> distributedObjects = hazelcastInstance.getDistributedObjects();
        final HazelcastObjectsReport.Builder builder = HazelcastObjectsReport.create();
        for ( DistributedObject object : distributedObjects )
        {
            if ( object instanceof IMap )
            {
                IMap<?, ?> map = hazelcastInstance.getMap( object.getName() );
                builder.addMapObject( MapObjectReport.create().name( DistributedObjectUtil.getName( object ) ).size( map.size() ).build() );
            }
            else if ( object instanceof IQueue )
            {
                IQueue<?> queue = hazelcastInstance.getQueue( object.getName() );
                builder.addQueueObject(
                    QueueObjectReport.create().name( DistributedObjectUtil.getName( object ) ).size( queue.size() ).build() );
            }
            else if ( object instanceof ITopic )
            {
                builder.addTopicObject( TopicObjectReport.create().name( DistributedObjectUtil.getName( object ) ).build() );
            }
            else if ( object instanceof IScheduledExecutorService )
            {
                final IScheduledExecutorService scheduledExecutorService = (IScheduledExecutorService) object;

                final ScheduledExecutorServiceObjectReport.Builder sbuilder = ScheduledExecutorServiceObjectReport.create();
                sbuilder.name( DistributedObjectUtil.getName( object ) );
                scheduledExecutorService.getAllScheduledFutures().forEach( ( key, value ) -> {

                    value.forEach( future -> {
                        final ScheduledTaskHandler handler = future.getHandler();
                        if ( handler != null ) // null handler means that task is already disposed
                        {
                            final String taskName = handler.getTaskName();
                            final String memberUuid = key.getUuid();
                            long totalRuns = -1;
                            long delaySeconds = -1;
                            Boolean isDone = null;
                            Boolean isCancelled = null;
                            try
                            {
                                totalRuns = future.getStats().getTotalRuns();
                                isDone = future.isDone();
                                //isCancelled = future.isCancelled();
                                delaySeconds = future.getDelay( TimeUnit.SECONDS);
                            }
                            catch ( Exception e )
                            {
                                LOG.debug( "Cannot get info for task {}", taskName );
                            }

                            sbuilder.task( new ScheduledTaskReport( memberUuid, taskName, totalRuns, delaySeconds, isDone, isCancelled ) );
                        }
                    } );
                } );
                builder.addScheduledExecutorServiceObject( sbuilder.build() );
            }
            else if ( object instanceof IExecutorService )
            {
                builder.addExecutorServiceObject(
                    ExecutorServiceObjectReport.create().name( DistributedObjectUtil.getName( object ) ).build() );
            }
            else if ( object instanceof FencedLock )
            {
                FencedLock lock = hazelcastInstance.getCPSubsystem().getLock( object.getName() );
                builder.addFencedLockObject( FencedLockObjectReport.create()
                                                 .name( DistributedObjectUtil.getName( object ) )
                                                 .locked( lock.isLocked() )
                                                 .lockCount( lock.getLockCount() )
                                                 .build() );
            }
        }

        return builder.build().toJson();
    }
}
