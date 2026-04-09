package com.enonic.xp.core.impl.hazelcast;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.core.internal.Local;

@Component
@Local(false)
public class HazelcastClusterServiceImpl
    implements ClusterService
{
    private final HazelcastInstance hazelcastInstance;

    private final ClusterAttributesApplier clusterAttributesApplier;

    @Activate
    public HazelcastClusterServiceImpl( final BundleContext bundleContext, @Reference final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
        this.clusterAttributesApplier = new ClusterAttributesApplier( bundleContext, hazelcastInstance );
    }

    @Activate
    public void activate()
    {
        this.clusterAttributesApplier.open();
    }

    @Deactivate
    public void deactivate()
    {
        this.clusterAttributesApplier.close();
    }

    @Override
    public boolean isLeader()
    {
        final Member localMember = hazelcastInstance.getCluster().getLocalMember();
        final Set<Member> members = hazelcastInstance.getCluster().getMembers();
        return !members.isEmpty() && members.iterator().next().equals( localMember );
    }

    @Override
    public boolean isLeader( final @NonNull ApplicationKey applicationKey )
    {
        final UUID localMemberUuid = hazelcastInstance.getCluster().getLocalMember().getUuid();

        final ReplicatedMap<UUID, Map<String, String>> attributes = hazelcastInstance.getReplicatedMap( ClusterAttributesApplier.MAP_NAME );

        final String appAttributeKey = ClusterAttributesApplier.APPLICATION_ATTRIBUTE_PREFIX + applicationKey;

        for ( final Member member : hazelcastInstance.getCluster().getMembers() )
        {
            final Map<String, String> memberAttributes = attributes.get( member.getUuid() );
            if ( memberAttributes != null && Boolean.TRUE.toString().equals( memberAttributes.get( appAttributeKey ) ) )
            {
                return member.getUuid().equals( localMemberUuid );
            }
        }

        return false;
    }
}
