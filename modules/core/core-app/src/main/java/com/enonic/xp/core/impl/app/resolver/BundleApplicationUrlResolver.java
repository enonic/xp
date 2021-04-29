package com.enonic.xp.core.impl.app.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.osgi.framework.Bundle;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;

public final class BundleApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final Bundle bundle;

    private final Supplier<Set<String>> files = Suppliers.memoize( this::doFindFiles );

    private final ConcurrentMap<String, Long> hashes = new ConcurrentHashMap<>();

    public BundleApplicationUrlResolver( final Bundle bundle )
    {
        this.bundle = bundle;
    }

    @Override
    public Set<String> findFiles()
    {
        return files.get();
    }

    @Override
    public long filesHash( final String path )
    {
        return this.hashes.computeIfAbsent( path, p -> doHash( p ) );
    }

    private ImmutableSet<String> doFindFiles()
    {
        return entriesStream( "/" ).map( url -> url.getFile().substring( 1 ) ).collect( ImmutableSet.toImmutableSet() );
    }

    private long doHash( final String path )
    {
        final HashingOutputStream hashingOutputStream =
            new HashingOutputStream( Hashing.farmHashFingerprint64(), OutputStream.nullOutputStream() );
        try (hashingOutputStream)
        {
            entriesStream( path ).forEach( url -> {
                try (InputStream stream = url.openStream())
                {
                    hashingOutputStream.write( url.getFile().getBytes( StandardCharsets.UTF_8 ) );
                    hashingOutputStream.write( 0 );
                    stream.transferTo( hashingOutputStream );
                }
                catch ( IOException e )
                {
                    throw new UncheckedIOException( e );
                }
            } );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        return hashingOutputStream.hash().asLong();
    }

    private Stream<URL> entriesStream( final String path )
    {
        final Iterator<URL> urls = this.bundle.findEntries( path, "*", true ).asIterator();
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( urls, Spliterator.ORDERED ), false )
            .filter( url -> !url.getPath().endsWith( "/" ) );
    }

    @Override
    public URL findUrl( final String path )
    {
        final URL url = this.bundle.getResource( path );
        return ( url == null || url.getPath().endsWith( "/" ) ) ? null : url;
    }
}
