package com.enonic.wem.api.home;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.google.common.base.Strings;

public final class HomeDir
{
    private final File dir;

    private HomeDir( final File dir )
    {
        this.dir = dir;
        System.out.println( "!! HOME_DIR = " + this.dir );
    }

    public File toFile()
    {
        return this.dir;
    }

    public String toString()
    {
        return this.dir.toString();
    }

    public static HomeDir get()
    {
        final BundleContext context = FrameworkUtil.getBundle( HomeDir.class ).getBundleContext();
        return get( context );
    }

    public static HomeDir get( final BundleContext context )
    {
        final String str = context.getProperty( "xp.home" );
        if ( Strings.isNullOrEmpty( str ) )
        {
            throw new IllegalArgumentException( "Home dir [xp.home] is not set." );
        }

        final File dir = new File( str );
        return new HomeDir( dir );
    }
}
