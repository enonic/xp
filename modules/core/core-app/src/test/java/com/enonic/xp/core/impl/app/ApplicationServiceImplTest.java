package com.enonic.xp.core.impl.app;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.Applications;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;

import static org.junit.Assert.*;

public class ApplicationServiceImplTest
    extends BundleBasedTest
{
    private ApplicationServiceImpl service;

    private ApplicationRepoServiceImpl repoService = Mockito.mock( ApplicationRepoServiceImpl.class );

    private EventPublisher eventPublisher;

    @Before
    public void initService()
    {
        this.service = new ApplicationServiceImpl();
        this.service.setRepoService( this.repoService );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.service.setEventPublisher( this.eventPublisher );
    }

    @Test
    public void testGetApplication()
        throws Exception
    {
        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.empty() );

        this.service.activate( getBundleContext() );

        final Bundle bundle = deployBundle( "app1", true );

        final Application result = this.service.getInstalledApplication( ApplicationKey.from( "app1" ) );
        assertNotNull( result );
        assertSame( bundle, result.getBundle() );
    }

    @Test(expected = ApplicationNotFoundException.class)
    public void testGetApplication_notFound()
    {
        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.empty() );

        this.service.activate( getBundleContext() );

        this.service.getInstalledApplication( ApplicationKey.from( "app1" ) );
    }

    @Test
    public void testGetAllApplications()
        throws Exception
    {
        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.empty() );

        this.service.activate( getBundleContext() );

        deployBundle( "app1", true );
        deployBundle( "app2", true );
        deployBundle( "app3", false );

        final Applications result = this.service.getInstalledApplications();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }

    @Test
    public void testGetApplicationKeys()
        throws Exception
    {
        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.empty() );

        this.service.activate( getBundleContext() );

        deployBundle( "app1", true );
        deployBundle( "app2", true );
        deployBundle( "app3", false );

        final ApplicationKeys result = this.service.getInstalledApplicationKeys();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( ApplicationKey.from( "app1" ) ) );
        assertTrue( result.contains( ApplicationKey.from( "app2" ) ) );
    }

    @Test
    public void testStartApplication()
        throws Exception
    {
        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.empty() );

        this.service.activate( getBundleContext() );

        final Bundle bundle = deployBundle( "app1", true );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ) );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    public void testStopApplication()
        throws Exception
    {
        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.empty() );

        this.service.activate( getBundleContext() );

        final Bundle bundle = deployBundle( "app1", true );
        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        this.service.stopApplication( ApplicationKey.from( "app1" ) );
        assertEquals( Bundle.RESOLVED, bundle.getState() );
    }

    @Test
    public void installApplication()
        throws Exception
    {
        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.empty() );

        this.service.activate( getBundleContext() );

        Mockito.when( this.repoService.createApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( Node.create().
                id( NodeId.from( "myNode" ) ).
                parentPath( NodePath.ROOT ).
                name( "myNode" ).
                build() );

        final InputStream in = newBundle( "my-bundle", true ).
            build();

        final ByteSource byteSource = ByteSource.wrap( ByteStreams.toByteArray( in ) );
        final Application application = this.service.installApplication( byteSource );

        assertNotNull( application );
        assertEquals( "my-bundle", application.getKey().getName() );
    }

    @Test
    public void updateBundle()
        throws Exception
    {
        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.empty() );

        this.service.activate( getBundleContext() );

        final Node node = Node.create().
            id( NodeId.from( "myNode" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            build();

        Mockito.when( this.repoService.createApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( node );

        Mockito.when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( node );

        final Application originalApplication =
            this.service.installApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( "my-bundle", true, "1.0.0" ).
                build() ) ) );

        Mockito.when( this.repoService.getApplicationNode( "my-bundle" ) ).
            thenReturn( node );

        final Application updatedApplication =
            this.service.installApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( "my-bundle", true, "1.0.1" ).
                build() ) ) );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
    }

    @Test
    public void install_applications_in_repo()
        throws Exception
    {
        final Node node = Node.create().
            id( NodeId.from( "myNodeId" ) ).
            name( "myBundle" ).
            parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).
            build();

        Mockito.when( this.repoService.getApplications() ).
            thenReturn( Nodes.create().
                add( node ).
                build() );

        Mockito.when( this.repoService.getApplicationSource( node.id() ) ).
            thenReturn( ByteSource.wrap( ByteStreams.toByteArray( newBundle( "my-bundle", true, "1.0.1" ).
                build() ) ) );

        Mockito.when( this.repoService.createApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).
            thenReturn( node );

        this.service.activate( getBundleContext() );

        Mockito.verify( this.repoService, Mockito.times( 1 ) ).getApplications();
        Mockito.verify( this.repoService, Mockito.times( 1 ) ).getApplicationSource( node.id() );
        Mockito.verify( this.repoService, Mockito.times( 1 ) ).createApplicationNode( Mockito.isA( Application.class ),
                                                                                      Mockito.isA( ByteSource.class ) );
        Mockito.verify( this.eventPublisher, Mockito.times( 1 ) ).publish( Mockito.isA( Event.class ) );

    }

    private Bundle deployBundle( final String key, final boolean isApp )
        throws Exception
    {
        final InputStream in = newBundle( key, isApp ).
            build();

        return deploy( key, in );
    }
}
