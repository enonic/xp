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
import org.osgi.framework.ServiceReference;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.app.Application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void addingService()
    {
        final ClusterAttributesApplier applier = new ClusterAttributesApplier( bundleContext, hazelcastInstance );

        final String result = applier.addingService( mockReference( "com.example.myapp" ) );

        assertEquals( "application-com.example.myapp", result );
        verify( replicatedMap ).put( uuid, Map.of( "application-com.example.myapp", "true" ) );
    }

    @Test
    void removedService()
    {
        final ClusterAttributesApplier applier = new ClusterAttributesApplier( bundleContext, hazelcastInstance );

        final ServiceReference<Application> reference = mockReference( "com.example.myapp" );

        applier.removedService( reference, applier.addingService( reference ) );

        final InOrder inOrder = inOrder( replicatedMap );
        inOrder.verify( replicatedMap ).put( uuid, Map.of( "application-com.example.myapp", "true" ) );
        inOrder.verify( replicatedMap ).put( uuid, Map.of() );
    }

    @Test
    void multipleApplications()
    {
        final ClusterAttributesApplier applier = new ClusterAttributesApplier( bundleContext, hazelcastInstance );

        applier.addingService( mockReference( "com.example.app1" ) );
        applier.addingService( mockReference( "com.example.app2" ) );

        final InOrder inOrder = inOrder( replicatedMap );
        inOrder.verify( replicatedMap ).put( uuid, Map.of( "application-com.example.app1", "true" ) );
        inOrder.verify( replicatedMap )
            .put( uuid, Map.of( "application-com.example.app1", "true", "application-com.example.app2", "true" ) );
    }

    @SuppressWarnings("unchecked")
    private static ServiceReference<Application> mockReference( final String symbolicName )
    {
        final Bundle bundle = mock( Bundle.class );
        when( bundle.getSymbolicName() ).thenReturn( symbolicName );

        final ServiceReference<Application> reference = mock( ServiceReference.class );
        when( reference.getBundle() ).thenReturn( bundle );

        return reference;
    }
}
