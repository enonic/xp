package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.osgi.framework.Bundle;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.UrlResource;

public final class BundleApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final Bundle bundle;

    private final Supplier<Set<String>> files = Suppliers.memoize( this::doFindFiles );

    public BundleApplicationUrlResolver( final Bundle bundle )
    {
        this.bundle = bundle;
    }

    @Override
    public Set<String> findFiles()
    {
        return files.get();
    }

    private ImmutableSet<String> doFindFiles()
    {
        return entriesStream( "/" ).map( url -> url.getFile().substring( 1 ) ).collect( ImmutableSet.toImmutableSet() );
    }

    private Stream<URL> entriesStream( final String path )
    {
        final Iterator<URL> urls = this.bundle.findEntries( path, "*", true ).asIterator();
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( urls, Spliterator.ORDERED ), false )
            .filter( url -> !url.getPath().endsWith( "/" ) );
    }

    @Override
    public Resource findResource( final String path )
    {
        final URL url = this.bundle.getResource( path );
        return ( url == null || url.getPath().endsWith( "/" ) )
            ? null
            : new UrlResource( ResourceKey.from( ApplicationHelper.getApplicationKey( bundle ), path ), url, "bundle" );
    }
}
