package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.blobstore.ZipDumpReadBlobStore;

public class ZipDumpReader
    extends AbstractDumpReader
{
    private final ZipFile zipFile;

    private ZipDumpReader( final SystemLoadListener listener, PathRef basePathInZip, ZipFile zipFile )
    {
        super( listener, new DefaultFilePaths( basePathInZip ), new ZipDumpReadBlobStore( zipFile, basePathInZip ) );
        this.zipFile = zipFile;
    }

    public static ZipDumpReader create( SystemLoadListener listener, final Path basePath, final String dumpName )
    {
        try
        {
            final SeekableByteChannel seekableByteChannel =
                Files.newByteChannel( basePath.resolve( dumpName + ".zip" ), EnumSet.of( StandardOpenOption.READ ) );
            final ZipFile zipFile = new ZipFile( seekableByteChannel );
            return new ZipDumpReader( listener, PathRef.of( dumpName ), zipFile );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    protected boolean exists( final PathRef file )
    {
        return zipFile.getEntry( file.asString() ) != null;
    }

    @Override
    protected InputStream openMetaFileStream( final PathRef metaFile )
        throws IOException
    {
        final ZipArchiveEntry entry = zipFile.getEntry( metaFile.asString() );
        return zipFile.getInputStream( entry );
    }

    @Override
    protected Stream<String> listDirectories( final PathRef repoRootPath )
    {
        final String prefix = repoRootPath.asString() + "/";
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( zipFile.getEntries().asIterator(), Spliterator.ORDERED ), false ).
            map( ZipArchiveEntry::getName ).
            filter( name -> name.startsWith( prefix ) ).
            filter( name -> name.indexOf( "/", prefix.length() ) != -1 ).
            map( name -> name.substring( prefix.length(), name.indexOf( "/", prefix.length() ) ) ).distinct();
    }

    @Override
    public void close()
        throws IOException
    {
        zipFile.close();
    }
}
