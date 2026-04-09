package com.enonic.xp.core.impl.hazelcast;

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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class ClusterAttributesApplierTest
{
    final UUID uuid = UUID.fromString( "a8a7ad1f-e1b8-46ff-9618-950e44daaaee" );

    @Mock
    BundleContext bundleContext;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HazelcastInstance hazelcastInstance;

    @Mock
    Member localMember;

    @Mock
    ReplicatedMap<UUID, Map<String, String>> replicatedMap;

    @BeforeEach
    void setUp()
    {
        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        when( localMember.getUuid() ).thenReturn( uuid );
        doReturn( replicatedMap ).when( hazelcastInstance ).getReplicatedMap( ClusterAttributesApplier.MAP_NAME );
    }

    @Test
    void addingBundle_application()
    {
        final Bundle bundle = mock( Bundle.class, withSettings().defaultAnswer( Answers.RETURNS_DEEP_STUBS ) );
        when( bundle.getHeaders().get( "X-Bundle-Type" ) ).thenReturn( "application" );
        when( bundle.getSymbolicName() ).thenReturn( "com.example.myapp" );

        final ClusterAttributesApplier applier = new ClusterAttributesApplier( bundleContext, hazelcastInstance );

        final Boolean result = applier.addingBundle( bundle, null );

        assertTrue( result );
        verify( replicatedMap ).put( uuid, Map.of( "application-com.example.myapp", "true" ) );
    }

    @Test
    void addingBundle_nonApplication()
    {
        final Bundle bundle = mock( Bundle.class );
        when( bundle.getHeaders() ).thenReturn( Dictionaries.of() );

        final ClusterAttributesApplier applier = new ClusterAttributesApplier( bundleContext, hazelcastInstance );

        final Boolean result = applier.addingBundle( bundle, null );

        assertNull( result );
        verifyNoInteractions( replicatedMap );
    }

    @Test
    void removedBundle()
    {
        final Bundle bundle = mock( Bundle.class, withSettings().defaultAnswer( Answers.RETURNS_DEEP_STUBS ) );
        when( bundle.getHeaders().get( "X-Bundle-Type" ) ).thenReturn( "application" );
        when( bundle.getSymbolicName() ).thenReturn( "com.example.myapp" );

        final ClusterAttributesApplier applier = new ClusterAttributesApplier( bundleContext, hazelcastInstance );

        applier.addingBundle( bundle, null );
        applier.removedBundle( bundle, null, true );

        final InOrder inOrder = inOrder( replicatedMap );
        inOrder.verify( replicatedMap ).put( uuid, Map.of( "application-com.example.myapp", "true" ) );
        inOrder.verify( replicatedMap ).put( uuid, Map.of() );
    }

    @Test
    void multipleApplications()
    {
        final Bundle bundle1 = mock( Bundle.class, withSettings().defaultAnswer( Answers.RETURNS_DEEP_STUBS ) );
        when( bundle1.getHeaders().get( "X-Bundle-Type" ) ).thenReturn( "application" );
        when( bundle1.getSymbolicName() ).thenReturn( "com.example.app1" );

        final Bundle bundle2 = mock( Bundle.class, withSettings().defaultAnswer( Answers.RETURNS_DEEP_STUBS ) );
        when( bundle2.getHeaders().get( "X-Bundle-Type" ) ).thenReturn( "application" );
        when( bundle2.getSymbolicName() ).thenReturn( "com.example.app2" );

        final ClusterAttributesApplier applier = new ClusterAttributesApplier( bundleContext, hazelcastInstance );

        applier.addingBundle( bundle1, null );
        applier.addingBundle( bundle2, null );

        final InOrder inOrder = inOrder( replicatedMap );
        inOrder.verify( replicatedMap ).put( uuid, Map.of( "application-com.example.app1", "true" ) );
        inOrder.verify( replicatedMap )
            .put( uuid, Map.of( "application-com.example.app1", "true", "application-com.example.app2", "true" ) );
    }
}
