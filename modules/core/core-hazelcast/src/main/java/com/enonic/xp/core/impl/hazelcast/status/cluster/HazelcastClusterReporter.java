package com.enonic.xp.core.impl.hazelcast.status.cluster;

import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class HazelcastClusterReporter
    extends JsonStatusReporter
{
    private final HazelcastInstance hazelcastInstance;

    @Activate
    public HazelcastClusterReporter( @Reference final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public String getName()
    {
        return "cluster.hazelcast";
    }

    @Override
    public JsonNode getReport()
    {

        final Cluster cluster = hazelcastInstance.getCluster();

        final HazelcastClusterReport.Builder builder = HazelcastClusterReport.create().
            clusterState( cluster.getClusterState().name() ).
            clusterTime( cluster.getClusterTime() ).
            clusterVersion( cluster.getClusterVersion().toString() );

        final Set<Member> members = cluster.getMembers();
        for ( Member member : members )
        {
            builder.addMember( HazelcastMemberState.create().
                address( member.getAddress().getHost() ).
                port( member.getAddress().getPort() ).
                version( member.getVersion().toString() ).
                uuid( member.getUuid() ).liteMember( member.isLiteMember() ).build() );
        }

        return builder.build().toJson();
    }
}
