package com.enonic.xp.launcher.provision;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Lists;

final class BundleInfoLoader
{
    private final File systemDir;

    public BundleInfoLoader( final File systemDir )
    {
        this.systemDir = systemDir;
    }

    public List<BundleInfo> load()
        throws Exception
    {
        final List<BundleInfo> list = Lists.newArrayList();
        final File file = new File( this.systemDir, "bundles.properties" );

        final Properties props = new Properties();
        props.load( new FileInputStream( file ) );

        build( list, props );

        Collections.sort( list );
        return list;
    }

    private void build( final List<BundleInfo> list, final Properties props )
        throws Exception
    {
        for ( final Map.Entry<Object, Object> entry : props.entrySet() )
        {
            build( list, entry.getKey().toString(), entry.getValue().toString() );
        }
    }

    private void build( final List<BundleInfo> list, final String uri, final String level )
        throws Exception
    {
        build( list, uri, Integer.parseInt( level ) );
    }

    private void build( final List<BundleInfo> list, final String uri, final int level )
        throws Exception
    {
        list.add( new BundleInfo( uri, level ) );
    }
}
