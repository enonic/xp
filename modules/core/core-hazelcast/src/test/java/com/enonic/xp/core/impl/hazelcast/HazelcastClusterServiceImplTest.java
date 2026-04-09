package com.enonic.xp.core.impl.hazelcast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastClusterServiceImplTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HazelcastInstance hazelcastInstance;

    @Mock
    BundleContext bundleContext;

    HazelcastClusterServiceImpl clusterService;

    @BeforeEach
    void setUp()
    {
        clusterService = new HazelcastClusterServiceImpl( bundleContext, hazelcastInstance );
    }

    @Test
    void isLeader_firstMember()
    {
        final Member localMember = mock( Member.class );
        final Member otherMember = mock( Member.class );

        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        when( hazelcastInstance.getCluster().getMembers() ).thenReturn( orderedSet( localMember, otherMember ) );

        assertTrue( clusterService.isLeader() );
    }

    @Test
    void isLeader_notFirstMember()
    {
        final Member localMember = mock( Member.class );
        final Member otherMember = mock( Member.class );

        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        when( hazelcastInstance.getCluster().getMembers() ).thenReturn( orderedSet( otherMember, localMember ) );

        assertFalse( clusterService.isLeader() );
    }

    @Test
    void isLeader_app_firstEligibleMember()
    {
        final UUID localUuid = UUID.randomUUID();

        final Member localMember = mockMember( localUuid );
        final Member otherMember = mock( Member.class );

        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        when( hazelcastInstance.getCluster().getMembers() ).thenReturn( orderedSet( localMember, otherMember ) );

        final ApplicationKey appKey = ApplicationKey.from( "com.example.myapp" );
        final ReplicatedMap<UUID, Map<String, String>> replicatedMap = mockReplicatedMap();
        when( replicatedMap.get( localUuid ) ).thenReturn( Map.of( "application-com.example.myapp", "true" ) );

        assertTrue( clusterService.isLeader( appKey ) );
    }

    @Test
    void isLeader_app_notFirstEligibleMember()
    {
        final UUID localUuid = UUID.randomUUID();
        final UUID otherUuid = UUID.randomUUID();

        final Member localMember = mockMember( localUuid );
        final Member otherMember = mockMember( otherUuid );

        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        when( hazelcastInstance.getCluster().getMembers() ).thenReturn( orderedSet( otherMember, localMember ) );

        final ApplicationKey appKey = ApplicationKey.from( "com.example.myapp" );
        final ReplicatedMap<UUID, Map<String, String>> replicatedMap = mockReplicatedMap();
        when( replicatedMap.get( otherUuid ) ).thenReturn( Map.of( "application-com.example.myapp", "true" ) );

        assertFalse( clusterService.isLeader( appKey ) );
    }

    @Test
    void isLeader_app_noMemberHasApp()
    {
        final UUID localUuid = UUID.randomUUID();

        final Member localMember = mockMember( localUuid );

        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        when( hazelcastInstance.getCluster().getMembers() ).thenReturn( orderedSet( localMember ) );

        final ApplicationKey appKey = ApplicationKey.from( "com.example.myapp" );
        final ReplicatedMap<UUID, Map<String, String>> replicatedMap = mockReplicatedMap();
        when( replicatedMap.get( localUuid ) ).thenReturn( null );

        assertFalse( clusterService.isLeader( appKey ) );
    }

    @Test
    void isLeader_app_skipsNonEligibleMembers()
    {
        final UUID member1Uuid = UUID.randomUUID();
        final UUID member2Uuid = UUID.randomUUID();
        final UUID localUuid = UUID.randomUUID();

        final Member member1 = mockMember( member1Uuid );
        final Member member2 = mockMember( member2Uuid );
        final Member localMember = mockMember( localUuid );

        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        when( hazelcastInstance.getCluster().getMembers() ).thenReturn( orderedSet( member1, member2, localMember ) );

        final ApplicationKey appKey = ApplicationKey.from( "com.example.myapp" );
        final ReplicatedMap<UUID, Map<String, String>> replicatedMap = mockReplicatedMap();
        when( replicatedMap.get( member1Uuid ) ).thenReturn( Map.of() );
        when( replicatedMap.get( member2Uuid ) ).thenReturn( null );
        when( replicatedMap.get( localUuid ) ).thenReturn( Map.of( "application-com.example.myapp", "true" ) );

        assertTrue( clusterService.isLeader( appKey ) );
    }

    @SuppressWarnings("unchecked")
    private ReplicatedMap<UUID, Map<String, String>> mockReplicatedMap()
    {
        final ReplicatedMap<UUID, Map<String, String>> map = mock( ReplicatedMap.class );
        doReturn( map ).when( hazelcastInstance ).getReplicatedMap( ClusterAttributesApplier.MAP_NAME );
        return map;
    }

    private static Member mockMember( final UUID uuid )
    {
        final Member member = mock( Member.class );
        when( member.getUuid() ).thenReturn( uuid );
        return member;
    }

    private static Set<Member> orderedSet( final Member... members )
    {
        return new LinkedHashSet<>( java.util.Arrays.asList( members ) );
    }
}
