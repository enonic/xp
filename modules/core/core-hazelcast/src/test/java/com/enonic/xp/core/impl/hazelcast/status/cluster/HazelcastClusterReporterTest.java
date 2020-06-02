package com.enonic.xp.core.impl.hazelcast.status.cluster;

import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;

import com.enonic.xp.status.JsonStatusReporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastClusterReporterTest
    extends JsonStatusReporterTest
{
    @Mock
    HazelcastInstance hazelcastInstance;

    @Mock
    Cluster cluster;

    @BeforeEach
    void setUp()
    {
        lenient().when( hazelcastInstance.getCluster() ).thenReturn( cluster );
    }

    @Test
    void getName()
    {
        final HazelcastClusterReporter hazelcastClusterReporter = new HazelcastClusterReporter( hazelcastInstance );
        assertEquals( "cluster.hazelcast", hazelcastClusterReporter.getName() );
    }

    @Test
    void getReport()
        throws Exception
    {
        final HazelcastClusterReporter hazelcastClusterReporter = new HazelcastClusterReporter( hazelcastInstance );
        when( cluster.getClusterState() ).thenReturn( ClusterState.IN_TRANSITION );
        when( cluster.getClusterVersion() ).thenReturn( Version.UNKNOWN );
        final Member member1 = mock( Member.class );
        when( member1.isLiteMember() ).thenReturn( false );
        when( member1.getAddress() ).thenReturn( new Address( "127.0.0.1", 5701 ) );
        when( member1.getVersion() ).thenReturn( MemberVersion.UNKNOWN );
        when( member1.getUuid() ).thenReturn( "member1-uuid" );

        final Member member2 = mock( Member.class );
        when( member2.isLiteMember() ).thenReturn( true );
        when( member2.getAddress() ).thenReturn( new Address( "127.0.0.2", 5702 ) );
        when( member2.getVersion() ).thenReturn( MemberVersion.UNKNOWN );
        when( member2.getUuid() ).thenReturn( "member2-uuid" );

        when( cluster.getMembers() ).thenReturn(
            new LinkedHashSet<>( List.of( member1, member2 ) ) ); //hazelcast always returns master first

        assertJson( "hazelcast_cluster.json", hazelcastClusterReporter.getReport() );
    }

    private void assertJson( final String fileName, final JsonNode actualJson )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( readFromFile( fileName ) );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( actualJson );

        assertEquals( expectedStr, actualStr );
    }
}
