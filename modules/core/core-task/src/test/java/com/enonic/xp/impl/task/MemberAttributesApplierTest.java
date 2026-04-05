package com.enonic.xp.impl.task;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberAttributesApplierTest
{
    final UUID uuid = UUID.fromString( "a8a7ad1f-e1b8-46ff-9618-950e44daaaee" );

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HazelcastInstance hazelcastInstance;

    @Mock
    Member localMember;

    @Mock
    ReplicatedMap<UUID, Map<String, String>> replicatedMap;

    TaskConfig config;

    @BeforeEach
    void setUp()
    {
        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        when( localMember.getUuid() ).thenReturn( uuid );
        doReturn( replicatedMap ).when( hazelcastInstance ).getReplicatedMap( TaskAttributesApplier.MAP_NAME );
        config = mock( TaskConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
    }

    @Test
    void lifecycle()
    {
        final TaskAttributesApplier taskAttributesApplier = new TaskAttributesApplier( hazelcastInstance );

        taskAttributesApplier.activate( config );

        var newConfig = mock( TaskConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( newConfig.distributable_acceptInbound() ).thenReturn( false );
        when( newConfig.distributable_acceptSystem() ).thenReturn( false );
        taskAttributesApplier.modify( newConfig );

        taskAttributesApplier.deactivate();

        final InOrder inOrder = inOrder( replicatedMap );
        inOrder.verify( replicatedMap ).put( uuid, Map.of( "tasks-enabled", "true", "system-tasks-enabled", "true" ) );
        inOrder.verify( replicatedMap ).put( uuid, Map.of( "tasks-enabled", "false", "system-tasks-enabled", "false" ) );
        inOrder.verify( replicatedMap ).remove( uuid );
    }
}
