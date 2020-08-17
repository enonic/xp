package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.VersionRange;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.Applications;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationServiceImplTest
    extends BundleBasedTest
{
    private final ApplicationRepoServiceImpl repoService = mock( ApplicationRepoServiceImpl.class );

    private ApplicationServiceImpl service;

    private ApplicationRegistryImpl applicationRegistry;

    private EventPublisher eventPublisher;

    @BeforeEach
    public void initService()
    {
        final BundleContext bundleContext = getBundleContext();
        this.applicationRegistry = new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub() );
        this.eventPublisher = mock( EventPublisher.class );
        this.service = new ApplicationServiceImpl( bundleContext, applicationRegistry, repoService, eventPublisher );
    }

    @Test
    public void get_application()
        throws Exception
    {
        final Bundle bundle = deployBundle( "app1", true );
        applicationRegistry.installApplication( bundle );

        final Application result = this.service.getInstalledApplication( ApplicationKey.from( "app1" ) );
        assertNotNull( result );
        assertSame( bundle, result.getBundle() );
    }

    @Test
    public void get_application_not_found()
    {
        assertNull( this.service.getInstalledApplication( ApplicationKey.from( "app1" ) ) );
    }

    @Test
    public void get_all_applications()
        throws Exception
    {
        final Bundle bundle1 = deployBundle( "app1", true );
        final Bundle bundle2 = deployBundle( "app2", true );
        deployBundle( "app3", false );

        applicationRegistry.installApplication( bundle1 );
        applicationRegistry.installApplication( bundle2 );

        final Applications result = this.service.getInstalledApplications();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }

    @Test
    public void get_application_keys()
        throws Exception
    {
        final Bundle bundle1 = deployBundle( "app1", true );
        final Bundle bundle2 = deployBundle( "app2", true );
        deployBundle( "app3", false );

        applicationRegistry.installApplication( bundle1 );
        applicationRegistry.installApplication( bundle2 );

        final ApplicationKeys result = this.service.getInstalledApplicationKeys();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( ApplicationKey.from( "app1" ) ) );
        assertTrue( result.contains( ApplicationKey.from( "app2" ) ) );
    }

    @Test
    public void start_application()
        throws Exception
    {
        final Bundle bundle = deployBundle( "app1", true );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ), false );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    public void start_app_atleast_version()
        throws Exception
    {
        // At a time of writing Felix version is 6.0.1. All greater versions should work as well.
        final Bundle bundle = deployBundle( "app1", true, VersionRange.valueOf( "6.0" ) );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ), false );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    public void start_app_version_range()
        throws Exception
    {
        // At a time of writing Felix version is 6.0.1. Range covers all future versions as well.
        final Bundle bundle = deployBundle( "app1", true, VersionRange.valueOf( "(6.0,9999.0]" ) );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ), false );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    public void start_app_invalid_version_range()
        throws Exception
    {
        // Version upper bound is too low for current and future Felix version (at a time of writing 6.0.1)
        final Bundle bundle = deployBundle( "app1", true, VersionRange.valueOf( "[5.1,5.2)" ) );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        assertThrows( ApplicationInvalidVersionException.class,
                      () -> this.service.startApplication( ApplicationKey.from( "app1" ), false ) );
    }

    @Test
    public void start_ex()
        throws Exception
    {
        // There is no version 0.0 of Felix.
        final Bundle bundle = deployBundle( "app1", true, VersionRange.valueOf( "[0.0,0.0]" ) );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        assertThrows( ApplicationInvalidVersionException.class,
                      () -> this.service.startApplication( ApplicationKey.from( "app1" ), false ) );
    }

    @Test
    public void stop_application()
        throws Exception
    {
        final Bundle bundle = deployBundle( "app1", true );

        applicationRegistry.installApplication( bundle );

        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        this.service.stopApplication( ApplicationKey.from( "app1" ), false );
        assertEquals( Bundle.RESOLVED, bundle.getState() );
    }

    @Test
    public void install_global()
        throws Exception
    {
        final Node applicationNode = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );

        final Application application = this.service.installGlobalApplication( byteSource, bundleName );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertFalse( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstalledEvents( applicationNode, times( 1 ) );
        verifyStartedEvent( application.getKey(), times( 1 ) );
    }

    @Test
    public void install_global_invalid()
        throws Exception
    {
        final Node applicationNode = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName, false );

        assertThrows( GlobalApplicationInstallException.class, () -> this.service.installGlobalApplication( byteSource, bundleName ) );
    }

    @Test
    public void install_local()
        throws Exception
    {
        final Node applicationNode = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );
        final Application application = this.service.installLocalApplication( byteSource, bundleName );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertTrue( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstalledEvents( applicationNode, Mockito.never() );
        verifyStartedEvent( application.getKey(), Mockito.never() );
    }

    @Test
    public void install_local_invalid()
        throws Exception
    {
        final Node applicationNode = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource source = createBundleSource( bundleName, false );

        assertThrows( LocalApplicationInstallException.class, () -> this.service.installLocalApplication( source, bundleName ) );
    }

    @Test
    public void update_installed_application()
        throws Exception
    {
        final Node node = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication =
            this.service.installGlobalApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).
                build() ) ), bundleName );

        mockRepoGetNode( node, bundleName );

        final Application updatedApplication =
            this.service.installGlobalApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).
                build() ) ), bundleName );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertFalse( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }

    @Test
    public void update_installed_local_application()
        throws Exception
    {
        final Node node = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication =
            this.service.installLocalApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).
                build() ) ), bundleName );

        final Application updatedApplication =
            this.service.installLocalApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).
                build() ) ), bundleName );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( updatedApplication.getKey() ) );

        verifyInstalledEvents( node, Mockito.never() );
        verifyStartedEvent( updatedApplication.getKey(), Mockito.never() );
    }

    @Test
    public void install_stored_application_not_found()
        throws Exception
    {
        assertThrows( ApplicationInstallException.class, () -> this.service.installStoredApplication( NodeId.from( "dummy" ) ) );
    }

    @Test
    public void install_stored_application()
        throws Exception
    {
        final Node node = Node.create().
            id( NodeId.from( "myNodeId" ) ).
            name( "myBundle" ).
            parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).
            build();

        final String bundleName = "my-bundle";

        when( this.repoService.getApplicationSource( node.id() ) ).
            thenReturn( createBundleSource( bundleName ) );

        final Application application = this.service.installStoredApplication( node.id() );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertFalse( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstalledEvents( node, Mockito.never() );
        verifyStartedEvent( application.getKey(), Mockito.never() );
    }

    @Test
    public void install_stored_applications()
        throws Exception
    {
        final Node node = Node.create().
            id( NodeId.from( "myNodeId" ) ).
            name( "myBundle" ).
            parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).
            build();

        final String bundleName = "my-bundle";

        ApplicationKey applicationKey = ApplicationKey.from( bundleName );

        when( this.repoService.getApplications() ).
            thenReturn( Nodes.from( node ) );

        when( this.repoService.getApplicationSource( node.id() ) ).
            thenReturn( createBundleSource( bundleName ) );

        this.service.installAllStoredApplications( ApplicationInstallationParams.create().triggerEvent( false ).build() );

        assertFalse( this.service.isLocalApplication( applicationKey ) );
        assertNotNull( this.service.getInstalledApplication( applicationKey ) );

        verifyInstalledEvents( node, Mockito.never() );
        verifyStartedEvent( applicationKey, Mockito.never() );
    }


    @Test
    public void uninstall_global_application()
        throws Exception
    {
        final Node node = Node.create().
            id( NodeId.from( "myNodeId" ) ).
            name( "myBundle" ).
            parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).
            build();

        final String bundleName = "my-bundle";

        when( this.repoService.getApplicationSource( node.id() ) ).
            thenReturn( createBundleSource( bundleName ) );

        final Application application = this.service.installStoredApplication( node.id() );

        this.service.uninstallApplication( application.getKey(), true );

        assertNull( this.service.getInstalledApplication( application.getKey() ) );

        Mockito.verify( this.eventPublisher, times( 1 ) ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstalled( application.getKey() ) ) ) );
    }

    @Test
    public void uninstall_local_application()
        throws Exception
    {
        final Node applicationNode = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );

        final ByteSource byteSource = createBundleSource( bundleName );
        final Application application = this.service.installLocalApplication( byteSource, bundleName );
        assertNotNull( this.service.getInstalledApplication( application.getKey() ) );

        this.service.uninstallApplication( application.getKey(), false );
        assertNull( this.service.getInstalledApplication( application.getKey() ) );

        Mockito.verify( this.eventPublisher, Mockito.never() ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstalled( application.getKey() ) ) ) );
    }

    @Test
    public void install_local_overriding_global()
        throws Exception
    {
        final Node node = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication =
            this.service.installGlobalApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).
                build() ) ), bundleName );

        assertFalse( this.service.isLocalApplication( originalApplication.getKey() ) );

        final Application updatedApplication =
            this.service.installLocalApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).
                build() ) ), bundleName );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( updatedApplication.getKey() ) );

        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }


    @Test
    public void uninstall_local_reinstall_global()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setBoolean( ApplicationPropertyNames.STARTED, true );

        final Node node = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication =
            this.service.installGlobalApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).
                build() ) ), bundleName );

        final ApplicationKey applicationKey = originalApplication.getKey();

        assertFalse( this.service.isLocalApplication( applicationKey ) );
        assertEquals( "1.0.0", originalApplication.getVersion().toString() );

        final Application updatedApplication =
            this.service.installLocalApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).
                build() ) ), bundleName );

        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );

        assertTrue( this.service.isLocalApplication( applicationKey ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( applicationKey ) );
        assertTrue( this.service.isLocalApplication( applicationKey ) );

        when( this.repoService.getApplicationSource( node.id() ) ).
            thenReturn( ByteSource.wrap( ByteStreams.toByteArray( newBundle( "my-bundle", true, "1.0.0" ).
                build() ) ) );

        this.service.uninstallApplication( updatedApplication.getKey(), false );

        assertEquals( originalApplication.getVersion(), this.service.getInstalledApplication( applicationKey ).getVersion() );
        assertFalse( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }

    @Test
    public void install_global_when_local_installed()
        throws Exception
    {
        final Node applicationNode = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );

        final ByteSource byteSource = createBundleSource( bundleName );

        final Application application = this.service.installLocalApplication( byteSource, bundleName );
        assertTrue( this.service.isLocalApplication( application.getKey() ) );

        when( this.repoService.getApplicationNode( application.getKey() ) ).
            thenReturn( applicationNode );

        this.service.installGlobalApplication( byteSource, bundleName );

        assertTrue( this.service.isLocalApplication( application.getKey() ) );

        verifyInstalledEvents( applicationNode, times( 1 ) );
    }

    private void verifyInstalledEvents( final Node node, final VerificationMode never )
    {
        Mockito.verify( this.eventPublisher, never ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.installed( node ) ) ) );
    }


    private void verifyStartedEvent( final ApplicationKey applicationKey, final VerificationMode never )
    {
        Mockito.verify( this.eventPublisher, never ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.started( applicationKey ) ) ) );
    }

    private void mockRepoCreateNode( final Node node )
    {
        when( this.repoService.createApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( node );
    }

    private void mockRepoGetNode( final Node applicationNode, final String appName )
    {
        when( this.repoService.getApplicationNode( ApplicationKey.from( appName ) ) ).
            thenReturn( applicationNode );
    }

    private ByteSource createBundleSource( final String bundleName )
        throws IOException
    {
        return createBundleSource( bundleName, true );
    }

    private ByteSource createBundleSource( final String bundleName, final boolean isApp )
        throws IOException
    {
        final InputStream in = newBundle( bundleName, isApp ).
            build();

        return ByteSource.wrap( ByteStreams.toByteArray( in ) );
    }

    private Bundle deployBundle( final String key, final boolean isApp )
        throws Exception
    {
        return this.deployBundle( key, isApp, null );
    }

    private Bundle deployBundle( final String key, final boolean isApp, final VersionRange systemVersionRange )
        throws Exception
    {
        final InputStream in = newBundle( key, isApp ).
            set( ApplicationHelper.X_SYSTEM_VERSION, systemVersionRange != null ? systemVersionRange.toString() : null ).
            build();

        return deploy( key, in );
    }

    @Test
    public void configuration_comes_first()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployBundle( "myapp", true );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        final Application app = service.getInstalledApplication( key );

        assertEquals( ConfigBuilder.create().add( "a", "b" ).build(), app.getConfig() );
    }

    @Test
    public void configuration_comes_last()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployBundle( "myapp", true );

        applicationRegistry.installApplication( bundle );

        final Application app = service.getInstalledApplication( key );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        assertEquals( ConfigBuilder.create().add( "a", "b" ).build(), app.getConfig() );
    }

    @Test
    public void configuration_comes_twice()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployBundle( "myapp", true );

        applicationRegistry.installApplication( bundle );

        final Application app = service.getInstalledApplication( key );

        final ApplicationInvalidator mock = mock( ApplicationInvalidator.class );
        applicationRegistry.addInvalidator( mock );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "c", "d" ).build() );

        assertEquals( ConfigBuilder.create().add( "c", "d" ).build(), app.getConfig() );
    }

    @Test
    public void configuration_comes_twice_invalidators_called()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployBundle( "myapp", true );

        applicationRegistry.installApplication( bundle );

        service.getInstalledApplication( key );

        final ApplicationInvalidator mock = mock( ApplicationInvalidator.class );
        applicationRegistry.addInvalidator( mock );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "c", "d" ).build() );

        verify( mock, times( 1 ) ).invalidate( eq( key ), eq( ApplicationInvalidationLevel.FULL ) );
    }

    private static class ApplicationEventMatcher
        implements ArgumentMatcher<Event>
    {
        Event thisObject;

        public ApplicationEventMatcher( Event thisObject )
        {
            this.thisObject = thisObject;
        }

        @Override
        public boolean matches( Event argument )
        {
            if ( argument == null || thisObject.getClass() != argument.getClass() )
            {
                return false;
            }

            return thisObject.getType().equals( argument.getType() ) && this.thisObject.getData().equals( argument.getData() );
        }
    }
}
