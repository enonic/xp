package com.enonic.xp.tools.gradle.watch;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

final class WatcherImpl
    implements Watcher
{
    private static final Kind[] EVENT_KIND = {ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE};

    private static final Logger LOGGER = LoggerFactory.getLogger( WatcherImpl.class );

    private WatchService service;

    private Map<Path, WatchKey> paths;

    private boolean closed;

    public WatcherImpl()
        throws IOException
    {
        this.service = FileSystems.getDefault().newWatchService();
        this.paths = new HashMap<>();
    }

    private void checkClosed()
    {
        if ( this.closed )
        {
            throw new ClosedWatchServiceException();
        }
    }

    @Override
    public void register( final Path path )
        throws IOException
    {
        checkClosed();

        if ( !Files.exists( path ) )
        {
            throw new NoSuchFileException( path.toString() );
        }

        if ( !Files.isDirectory( path ) )
        {
            Path dir = path.getParent();
            this.paths.put( dir, dir.register( this.service, EVENT_KIND, HIGH ) );
            if ( LOGGER.isDebugEnabled() )
            {
                LOGGER.debug( "{} is registered.", dir );
            }
            return;
        }

        Files.walkFileTree( path, new SimpleFileVisitor<Path>()
        {
            public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs )
                throws IOException
            {
                paths.put( dir, dir.register( service, EVENT_KIND, HIGH ) );
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "{} is registered.", dir );
                }
                return FileVisitResult.CONTINUE;
            }
        } );
    }

    @Override
    public void unregister( final Path path )
        throws IOException
    {
        checkClosed();

        final Iterator<Entry<Path, WatchKey>> it = this.paths.entrySet().iterator();
        while ( it.hasNext() )
        {
            final Entry<Path, WatchKey> e = it.next();
            final Path p = e.getKey();
            if ( !p.startsWith( path ) )
            {
                continue;
            }

            if ( LOGGER.isDebugEnabled() )
            {
                LOGGER.debug( "{} is unregistered", p );
            }

            e.getValue().cancel();
            it.remove();
        }

        for ( final Entry<Path, WatchKey> e : this.paths.entrySet() )
        {
            if ( e.getValue().isValid() )
            {
                continue;
            }

            final Path p = e.getKey();
            this.paths.put( p, p.register( this.service, EVENT_KIND, HIGH ) );
        }
    }

    @Override
    public boolean isWatching( final Path path )
    {
        return this.paths.containsKey( path );
    }

    @Override
    public WatchKey take()
        throws InterruptedException
    {
        checkClosed();
        return this.service.take();
    }

    @Override
    public void close()
        throws IOException
    {
        this.closed = true;
        this.paths.clear();
        this.service.close();
    }

    @Override
    public boolean isOpen()
    {
        return !this.closed;
    }
}
