package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStoreUtils;

public class ZipDumpReader
    extends AbstractDumpReader
{

    private static final Pattern ROOT_DUMP_DIR_PATTERN = Pattern.compile( "^([^/]+)\\/dump\\.json$" );

    private final ZipFile zipFile;

    private ZipDumpReader( final SystemLoadListener listener, final PathRef basePathInZip, final ZipFile zipFile )
    {
        super( listener, new DefaultFilePaths( basePathInZip ),
               reference -> new ZipEntryByteSource( zipFile, DumpBlobStoreUtils.getBlobPathRef( basePathInZip, reference ).asString() ) );
        this.zipFile = zipFile;
    }

    public static ZipDumpReader create( SystemLoadListener listener, final Path basePath, final String dumpName )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );
        try
        {
            final SeekableByteChannel seekableByteChannel =
                Files.newByteChannel( basePath.resolve( dumpName + ".zip" ), EnumSet.of( StandardOpenOption.READ ) );
            final ZipFile zipFile = ZipFile.builder().setSeekableByteChannel( seekableByteChannel ).get();

            return create( listener, dumpName, zipFile );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static ZipDumpReader create( final SystemLoadListener listener, final String dumpName, final ZipFile zipFile )
    {
        if ( zipFile.getEntry( "dump.json" ) != null )
        {
            return new ZipDumpReader( listener, PathRef.of(), zipFile );
        }
        else if ( zipFile.getEntry( dumpName + "/dump.json" ) != null )
        {
            return new ZipDumpReader( listener, PathRef.of( dumpName ), zipFile );
        }
        else
        {
            final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

            while ( entries.hasMoreElements() )
            {
                final ZipArchiveEntry entry = entries.nextElement();

                final Matcher matcher = ROOT_DUMP_DIR_PATTERN.matcher( entry.getName() );

                if ( matcher.matches() )
                {
                    return new ZipDumpReader( listener, PathRef.of( matcher.group( 1 ) ), zipFile );
                }
            }

            throw new RepoLoadException( "Archive is not a valid dump archive: [" + dumpName + "]" );
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
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( zipFile.getEntries().asIterator(), Spliterator.ORDERED ), false )
            .map( ZipArchiveEntry::getName )
            .filter( name -> name.startsWith( prefix ) )
            .filter( name -> name.indexOf( '/', prefix.length() ) != -1 )
            .map( name -> name.substring( prefix.length(), name.indexOf( '/', prefix.length() ) ) )
            .distinct();
    }

    @Override
    public void close()
        throws IOException
    {
        zipFile.close();
    }

    private static class ZipEntryByteSource
        extends ByteSource
    {
        final String zipEntryName;

        final ZipFile zipFile;

        ZipEntryByteSource( final ZipFile zipFile, final String zipEntryName )
        {
            this.zipFile = zipFile;
            this.zipEntryName = zipEntryName;
        }

        @Override
        public InputStream openStream()
            throws IOException
        {
            return zipFile.getInputStream( zipFile.getEntry( zipEntryName ) );
        }
    }
}
