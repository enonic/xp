package com.enonic.xp.core.impl.hazelcast.status.cluster;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.net.MediaType;
import com.hazelcast.cluster.Address;
import com.hazelcast.cluster.Cluster;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastClusterReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

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
        when( member1.getUuid() ).thenReturn( UUID.fromString( "fcb2e135-1f61-4100-af3a-95449c7e6e26" ) );

        final Member member2 = mock( Member.class );
        when( member2.isLiteMember() ).thenReturn( true );
        when( member2.getAddress() ).thenReturn( new Address( "127.0.0.2", 5702 ) );
        when( member2.getVersion() ).thenReturn( MemberVersion.UNKNOWN );
        when( member2.getUuid() ).thenReturn( UUID.fromString( "98c640ab-66e5-48c6-8e64-e5aeb352dfaf" ) );

        when( cluster.getMembers() ).thenReturn(
            new LinkedHashSet<>( List.of( member1, member2 ) ) ); //hazelcast always returns master first

        assertJson( "hazelcast_cluster.json", hazelcastClusterReporter );
    }

    private void assertJson( final String fileName, StatusReporter reporter )
        throws Exception
    {
        assertEquals( MediaType.JSON_UTF_8, reporter.getMediaType() );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );

        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( fileName ),
                                         jsonTestHelper.bytesToJson( outputStream.toByteArray() ) );
    }
}
