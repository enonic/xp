package com.enonic.xp.launcher.impl.provision;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class BundleInfoFinder
{
    private final File systemDir;

    BundleInfoFinder( final File systemDir )
    {
        this.systemDir = systemDir;
    }

    List<BundleInfo> find()
        throws Exception
    {
        final Set<BundleInfo> set = new HashSet<>();
        for ( final Map.Entry<Integer, File> entry : findBundleDirs().entrySet() )
        {
            findBundles( set, entry.getValue(), entry.getKey() );
        }

        final List<BundleInfo> list = new ArrayList<>( set );
        Collections.sort( list );
        return list;
    }

    private Map<Integer, File> findBundleDirs()
    {
        final Map<Integer, File> result = new HashMap<>();
        final File[] dirs = this.systemDir.listFiles( this::isDirectory );
        if ( dirs == null )
        {
            return result;
        }

        for ( final File dir : dirs )
        {
            try
            {
                result.put( Integer.parseInt( dir.getName() ), dir );
            }
            catch ( final Exception e )
            {
                // Do nothing
            }
        }

        return result;
    }

    private void findBundles( final Set<BundleInfo> set, final File dir, final int runLevel )
    {
        final File[] files = dir.listFiles( this::isJarFile );
        if ( files == null )
        {
            return;
        }

        for ( final File file : files )
        {
            set.add( new BundleInfo( file, runLevel ) );
        }
    }

    private boolean isDirectory( final File file )
    {
        return file.isDirectory();
    }

    private boolean isJarFile( final File file )
    {
        return file.isFile() && file.getName().endsWith( ".jar" );
    }
}
