package com.enonic.xp.impl.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class MemberAttributesApplierTest
{
    @Mock
    BundleContext bundleContext;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HazelcastInstance hazelcastInstance;

    @Mock
    Member localMember;

    TaskConfig config;

    @BeforeEach
    void setUp()
    {
        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
        config = mock( TaskConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
    }

    @Test
    void lifecycle()
    {
        final MemberAttributesApplier memberAttributesApplier = new MemberAttributesApplier( bundleContext, hazelcastInstance );

        memberAttributesApplier.activate( config );

        verify( localMember ).setBooleanAttribute( "tasks-enabled", true );
        verify( localMember ).setBooleanAttribute( "system-tasks-enabled", true );

        when( config.distributable_acceptInbound() ).thenReturn( false );
        when( config.distributable_acceptSystem() ).thenReturn( false );
        memberAttributesApplier.modify( config );
        verify( localMember ).setBooleanAttribute( "tasks-enabled", false );
        verify( localMember ).setBooleanAttribute( "system-tasks-enabled", false );

        memberAttributesApplier.deactivate();
        verify( localMember ).removeAttribute( "tasks-enabled" );
        verify( localMember ).removeAttribute( "system-tasks-enabled" );
    }

    @Test
    void addingBundle()
    {
        final Bundle bundle = mock( Bundle.class, withSettings().defaultAnswer( Answers.RETURNS_DEEP_STUBS ) );
        when( bundle.getHeaders().get( "X-Bundle-Type" ) ).thenReturn( "application" );
        when( bundle.getSymbolicName() ).thenReturn( "some.app" );

        final MemberAttributesApplier memberAttributesApplier = new MemberAttributesApplier( bundleContext, hazelcastInstance );
        memberAttributesApplier.addingBundle( bundle, null );
        verify( localMember ).setBooleanAttribute( MemberAttributesApplier.TASKS_ENABLED_ATTRIBUTE_PREFIX + "some.app", true );
    }

    @Test
    void addingBundle_noApp()
    {
        final Bundle bundle = mock( Bundle.class, withSettings().defaultAnswer( Answers.RETURNS_DEEP_STUBS ) );

        final MemberAttributesApplier memberAttributesApplier = new MemberAttributesApplier( bundleContext, hazelcastInstance );
        memberAttributesApplier.addingBundle( bundle, null );
        verifyNoInteractions( localMember );
    }

    @Test
    void removingBundle()
    {
        final Bundle bundle = mock( Bundle.class );
        when( bundle.getSymbolicName() ).thenReturn( "some.app" );
        final MemberAttributesApplier memberAttributesApplier = new MemberAttributesApplier( bundleContext, hazelcastInstance );
        memberAttributesApplier.removedBundle( bundle, null, null );
        verify( localMember ).removeAttribute( MemberAttributesApplier.TASKS_ENABLED_ATTRIBUTE_PREFIX + "some.app" );
    }
}
