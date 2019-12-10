package com.enonic.xp.launcher.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.reflect.ClassPath;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class OsgiExportsBuilder
{
    private final static String WILDCARD_PATTERN = ".*";

    private final Set<String> packageNames;

    public OsgiExportsBuilder( final ClassLoader loader )
    {
        this.packageNames = findAllPackages( loader );
    }

    public String expandExports( final String exports )
    {
        final Iterable<String> list = Splitter.on( ',' ).trimResults().omitEmptyStrings().split( exports );
        final Iterable<String> result = expandExports( list );
        return Joiner.on( ',' ).join( result );
    }

    private Iterable<String> expandExports( final Iterable<String> list )
    {
        final List<String> result = new ArrayList<>();
        for ( final String item : list )
        {
            expandExports( result, item );
        }

        return result;
    }

    private void expandExports( final List<String> result, final String item )
    {
        final int pos = item.indexOf( ';' );
        final String name = ( pos > -1 ) ? item.substring( 0, pos ) : item;
        final String options = ( pos > -1 ) ? item.substring( pos ) : null;
        result.addAll( findPackages( name ).stream().map( epanded -> createExport( epanded, options ) ).collect( Collectors.toList() ) );
    }

    private String createExport( final String name, final String options )
    {
        if ( !isNullOrEmpty( options ) )
        {
            return name + options;
        }
        else
        {
            return name;
        }
    }

    private Set<String> findPackages( final String name )
    {
        if ( name.endsWith( WILDCARD_PATTERN ) )
        {
            return findPackages( name.substring( 0, name.length() - WILDCARD_PATTERN.length() ), true );
        }
        else
        {
            return findPackages( name, false );
        }
    }

    private Set<String> findPackages( final String name, final boolean recursive )
    {
        if ( !recursive )
        {
            return Set.of( name );
        }

        return this.packageNames.stream().filter( item -> matchesPackage( name, item ) ).collect( Collectors.toSet() );
    }

    private boolean matchesPackage( final String name, final String item )
    {
        return name.equals( item ) || item.startsWith( name + "." );
    }

    private Set<String> findAllPackages( final ClassLoader loader )
    {
        try
        {
            return doFindAllPackages( loader );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private Set<String> doFindAllPackages( final ClassLoader loader )
        throws Exception
    {
        final ClassPath classPath = ClassPath.from( loader );
        return classPath.getAllClasses().stream().map( ClassPath.ClassInfo::getPackageName ).collect( Collectors.toSet() );
    }
}
