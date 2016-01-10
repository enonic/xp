package com.enonic.xp.core.impl.app;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.util.Exceptions;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService, ApplicationInvalidator
{
    private ApplicationRegistry registry;

    private BundleContext context;

    @Activate
    public void activate( final BundleContext context )
    {
        this.registry = new ApplicationRegistry( context );
        this.context = context;
    }

    @Override
    public Application getApplication( final ApplicationKey key )
        throws ApplicationNotFoundException
    {
        final Application application = this.registry.get( key );
        if ( application == null )
        {
            throw new ApplicationNotFoundException( key );
        }
        return application;
    }

    @Override
    public ApplicationKeys getApplicationKeys()
    {
        return this.registry.getKeys();
    }

    @Override
    public Applications getAllApplications()
    {
        return Applications.from( this.registry.getAll() );
    }

    @Override
    public void startApplication( final ApplicationKey key )
    {
        startApplication( getApplication( key ) );
    }

    @Override
    public void stopApplication( final ApplicationKey key )
    {
        stopApplication( getApplication( key ) );
    }

    @Override
    public Application installApplication( final ByteSource byteSource )
    {
        final File tmpFile = writeAsTmpFile( byteSource );

        final String symbolicName = findSymbolicName( tmpFile );

        final Bundle bundle = installBundle( tmpFile, symbolicName );

        tmpFile.delete();

        return this.registry.get( ApplicationKey.from( bundle ) );
    }

    private Bundle installBundle( final File tmpFile, final String symbolicName )
    {
        try
        {
            return this.context.installBundle( symbolicName, Files.asByteSource( tmpFile ).openStream() );
        }
        catch ( Exception e )
        {
            throw new InstallApplicationException( "Could not install application '" + symbolicName + "'", e );
        }
    }

    private File writeAsTmpFile( final ByteSource byteSource )
    {
        File targetFile;
        try
        {
            targetFile = File.createTempFile( createTmpFileName(), ".jar" );
            byteSource.copyTo( Files.asByteSink( targetFile ) );
        }
        catch ( IOException e )
        {
            throw new InstallApplicationException( "Could not read file", e );
        }

        return targetFile;
    }

    private String createTmpFileName()
    {
        return System.currentTimeMillis() + "";
    }

    private String findSymbolicName( final File file )
    {
        final Manifest man;

        try
        {
            final JarFile jarFile = new JarFile( file );
            man = jarFile.getManifest();
            jarFile.close();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Could not find symbolic name from bundle-file", e );
        }

        return man.getMainAttributes().getValue( Constants.BUNDLE_SYMBOLICNAME );
    }


    private void startApplication( final Application application )
    {
        try
        {
            application.getBundle().start();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private void stopApplication( final Application application )
    {
        try
        {
            application.getBundle().stop();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.registry.invalidate( key );
    }
}
