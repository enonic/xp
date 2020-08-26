package com.enonic.xp.core.impl.hazelcast.status.objects;

import java.util.Collection;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.cp.lock.FencedLock;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class HazelcastObjectsReporter
    extends JsonStatusReporter
{
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
    public JsonNode getReport()
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
                builder.addQueueObject( QueueObjectReport.create().
                    name( DistributedObjectUtil.getName( object ) ).
                    size( queue.size() ).
                    build() );
            }
            else if ( object instanceof ITopic )
            {
                builder.addTopicObject( TopicObjectReport.create().
                    name( DistributedObjectUtil.getName( object ) ).
                    build() );
            }
            else if ( object instanceof IExecutorService )
            {
                builder.addExecutorServiceObject( ExecutorServiceObjectReport.create().
                    name( DistributedObjectUtil.getName( object ) ).
                    build() );
            }
            else if ( object instanceof FencedLock )
            {
                FencedLock lock = hazelcastInstance.getCPSubsystem().getLock( object.getName() );
                builder.addFencedLockObject( FencedLockObjectReport.create().
                    name( DistributedObjectUtil.getName( object ) ).
                    locked( lock.isLocked() ).
                    lockCount( lock.getLockCount() ).
                    build() );
            }
        }

        return builder.build().toJson();
    }
}
