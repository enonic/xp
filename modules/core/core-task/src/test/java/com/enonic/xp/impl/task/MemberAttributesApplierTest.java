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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.core.internal.Dictionaries;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class MemberAttributesApplierTest
{
    final UUID uuid = UUID.fromString( "a8a7ad1f-e1b8-46ff-9618-950e44daaaee" );

    @Mock
    BundleContext bundleContext;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HazelcastInstance hazelcastInstance;

    @Mock
    Member localMember;

    @Mock
    ReplicatedMap<Object, Object> replicatedMap;

    TaskConfig config;

    @BeforeEach
    void setUp()
    {
        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );

        when( localMember.getUuid() ).thenReturn( uuid );
        when( hazelcastInstance.getReplicatedMap( MemberAttributesApplier.MAP_NAME ) ).thenReturn( replicatedMap );
        config = mock( TaskConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
    }

    @Test
    void lifecycle()
    {
        final MemberAttributesApplier memberAttributesApplier = new MemberAttributesApplier( bundleContext, hazelcastInstance );

        memberAttributesApplier.activate( config );

        var newConfig = mock( TaskConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( newConfig.distributable_acceptInbound() ).thenReturn( false );
        when( newConfig.distributable_acceptSystem() ).thenReturn( false );
        memberAttributesApplier.modify( newConfig );

        memberAttributesApplier.deactivate();

        verify( replicatedMap, times( 2 ) ).put( eq( uuid ), any( Map.class ) );
        final InOrder inOrder = inOrder( replicatedMap );

        inOrder.verify( replicatedMap ).put( uuid, Map.of( "tasks-enabled", "true", "system-tasks-enabled", "true" ) );
        inOrder.verify( replicatedMap ).put( uuid, Map.of( "tasks-enabled", "false", "system-tasks-enabled", "false" ) );
        inOrder.verify( replicatedMap ).remove( uuid );
    }

    @Test
    void addingBundle()
    {
        final Bundle bundle = mock( Bundle.class, withSettings().defaultAnswer( Answers.RETURNS_DEEP_STUBS ) );
        when( bundle.getHeaders().get( "X-Bundle-Type" ) ).thenReturn( "application" );
        when( bundle.getSymbolicName() ).thenReturn( "some.app" );

        final MemberAttributesApplier memberAttributesApplier = new MemberAttributesApplier( bundleContext, hazelcastInstance );
        memberAttributesApplier.activate( config );
        memberAttributesApplier.addingBundle( bundle, null );

        final InOrder inOrder = inOrder( replicatedMap );

        inOrder.verify( replicatedMap ).put( uuid, Map.of( "tasks-enabled", "true", "system-tasks-enabled", "true" ) );
        inOrder.verify( replicatedMap )
            .put( uuid, Map.of( "tasks-enabled", "true", "system-tasks-enabled", "true",
                                MemberAttributesApplier.TASKS_ENABLED_ATTRIBUTE_PREFIX + "some.app", "true" ) );
    }

    @Test
    void addingBundle_noApp()
    {
        final Bundle bundle = mock( Bundle.class );
        when( bundle.getHeaders() ).thenReturn( Dictionaries.of() );

        final MemberAttributesApplier memberAttributesApplier = new MemberAttributesApplier( bundleContext, hazelcastInstance );
        memberAttributesApplier.addingBundle( bundle, null );
        verifyNoInteractions( replicatedMap );
    }

    @Test
    void removingBundle()
    {
        final Bundle bundle = mock( Bundle.class );
        when( bundle.getSymbolicName() ).thenReturn( "some.app" );
        when( bundle.getHeaders() ).thenReturn( Dictionaries.of() );

        final MemberAttributesApplier memberAttributesApplier = new MemberAttributesApplier( bundleContext, hazelcastInstance );

        memberAttributesApplier.activate( config );
        memberAttributesApplier.addingBundle( bundle, null );
        memberAttributesApplier.removedBundle( bundle, null, null );
        verify( replicatedMap ).put( uuid, Map.of( "tasks-enabled", "true", "system-tasks-enabled", "true" ) );
    }
}
