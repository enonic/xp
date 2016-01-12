package com.enonic.xp.core.impl.app;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
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

import static org.junit.Assert.*;

public class ApplicationServiceImplTest
    extends BundleBasedTest
{
    private ApplicationServiceImpl service;

    private ApplicationRepoServiceImpl repoService;

    @Before
    public void initService()
    {
        this.service = new ApplicationServiceImpl();
        this.service.activate( getBundleContext() );
        this.repoService = Mockito.mock( ApplicationRepoServiceImpl.class );
        this.service.setRepoService( this.repoService );
    }

    @Test
    public void testGetApplication()
        throws Exception
    {
        final Bundle bundle = deployBundle( "app1", true );

        final Application result = this.service.getApplication( ApplicationKey.from( "app1" ) );
        assertNotNull( result );
        assertSame( bundle, result.getBundle() );
    }

    @Test(expected = ApplicationNotFoundException.class)
    public void testGetApplication_notFound()
    {
        this.service.getApplication( ApplicationKey.from( "app1" ) );
    }

    @Test
    public void testGetAllApplications()
        throws Exception
    {
        deployBundle( "app1", true );
        deployBundle( "app2", true );
        deployBundle( "app3", false );

        final Applications result = this.service.getAllApplications();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }

    @Test
    public void testGetApplicationKeys()
        throws Exception
    {
        deployBundle( "app1", true );
        deployBundle( "app2", true );
        deployBundle( "app3", false );

        final ApplicationKeys result = this.service.getApplicationKeys();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( ApplicationKey.from( "app1" ) ) );
        assertTrue( result.contains( ApplicationKey.from( "app2" ) ) );
    }

    @Test
    public void testStartApplication()
        throws Exception
    {
        final Bundle bundle = deployBundle( "app1", true );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ) );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    public void testStopApplication()
        throws Exception
    {
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
        final InputStream in = newBundle( "my-bundle", true ).
            build();

        final Application application = this.service.installApplication( ByteSource.wrap( ByteStreams.toByteArray( in ) ) );

        assertNotNull( application );
        assertEquals( "my-bundle", application.getKey().getName() );
    }

    @Test
    public void installIsNotApplication()
        throws Exception
    {
        final InputStream in = newBundle( "my-bundle", true ).
            build();

        final Application application = this.service.installApplication( ByteSource.wrap( ByteStreams.toByteArray( in ) ) );

        assertNotNull( application );
        assertEquals( "my-bundle", application.getKey().getName() );
    }

    @Ignore
    @Test
    public void updateBundle()
        throws Exception
    {
        final Application application =
            this.service.installApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( "my-bundle", true, "1.0.0" ).
                build() ) ) );

        final Application updatedApplication =
            this.service.installApplication( ByteSource.wrap( ByteStreams.toByteArray( newBundle( "my-bundle", true, "1.0.1" ).
                build() ) ) );

        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
    }


    private Bundle deployBundle( final String key, final boolean isApp )
        throws Exception
    {
        final InputStream in = newBundle( key, isApp ).
            build();

        return deploy( key, in );
    }
}
