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
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.blobstore.BlobReference;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStoreUtils;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositorySegmentUtils;

public class ZipDumpReaderV7
    implements DumpReaderV7
{
    private static final Pattern ROOT_DUMP_DIR_PATTERN = Pattern.compile( "^([^/]+)\\/dump\\.json$" );

    private final ZipFile zipFile;

    private final PathRef basePathInZip;

    private final FilePaths filePaths;

    private ZipDumpReaderV7( final PathRef basePathInZip, final ZipFile zipFile )
    {
        this.zipFile = zipFile;
        this.basePathInZip = basePathInZip;
        this.filePaths = new DefaultFilePaths( basePathInZip );
    }

    public static ZipDumpReaderV7 create( final Path basePath, final String dumpName )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );
        try
        {
            final SeekableByteChannel seekableByteChannel =
                Files.newByteChannel( basePath.resolve( dumpName + ".zip" ), EnumSet.of( StandardOpenOption.READ ) );
            final ZipFile zipFile = ZipFile.builder().setSeekableByteChannel( seekableByteChannel ).get();

            return create( dumpName, zipFile );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static ZipDumpReaderV7 create( final String dumpName, final ZipFile zipFile )
    {
        if ( zipFile.getEntry( "dump.json" ) != null )
        {
            return new ZipDumpReaderV7( PathRef.of(), zipFile );
        }
        else if ( zipFile.getEntry( dumpName + "/dump.json" ) != null )
        {
            return new ZipDumpReaderV7( PathRef.of( dumpName ), zipFile );
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
                    return new ZipDumpReaderV7( PathRef.of( matcher.group( 1 ) ), zipFile );
                }
            }

            throw new RepoLoadException( "Archive is not a valid dump archive: [" + dumpName + "]" );
        }
    }

    public RepositoryIds getRepositories()
    {
        final PathRef repoRootPath = filePaths.repoRootPath();

        return listDirectories( repoRootPath ).map( RepositoryId::from ).collect( RepositoryIds.collector() );
    }

    public Branches getBranches( final RepositoryId repositoryId )
    {
        final PathRef branchRootPath = filePaths.branchRootPath( repositoryId );

        return listDirectories( branchRootPath ).map( Branch::from ).collect( Branches.collector() );
    }

    public NodeStoreVersion get( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        final ByteSource dataBytes = getBlobByteSource( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL, nodeVersionKey.getNodeBlobKey() );

        final ByteSource indexConfigBytes =
            getBlobByteSource( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL, nodeVersionKey.getIndexConfigBlobKey() );

        final ByteSource accessControlBytes =
            getBlobByteSource( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL, nodeVersionKey.getAccessControlBlobKey() );

        try
        {
            return NodeVersionJsonSerializer.toNodeVersion( dataBytes, indexConfigBytes, accessControlBytes );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read node version", e );
        }
    }

    public DumpMeta getDumpMeta()
    {
        try
        {
            try (InputStream stream = openMetaFileStream( filePaths.metaDataFile() ))
            {
                return new DumpMetaJsonSerializer().toDumpMeta( stream );
            }
        }
        catch ( IOException e )
        {
            throw new RepoLoadException( "Cannot read dump-meta file", e );
        }
    }

    public BlobRecord getRecord( final Segment segment, final BlobKey key )
    {
        return new ZipDumpBlobRecord( segment, key );
    }

    public Optional<PathRef> getBranchEntries( final RepositoryId repositoryId, final Branch branch )
    {
        final PathRef pathRef = filePaths.branchMetaPath( repositoryId, branch );
        return exists( pathRef ) ? Optional.of( pathRef ) : Optional.empty();
    }

    public Optional<PathRef> getVersions( final RepositoryId repositoryId )
    {
        final PathRef pathRef = filePaths.versionMetaPath( repositoryId );
        return exists( pathRef ) ? Optional.of( pathRef ) : Optional.empty();
    }

    public Optional<PathRef> getCommits( final RepositoryId repositoryId )
    {
        final PathRef pathRef = filePaths.commitMetaPath( repositoryId );
        return exists( pathRef ) ? Optional.of( pathRef ) : Optional.empty();
    }

    public TarArchiveInputStream openTarStream( final PathRef metaFile )
        throws IOException
    {
        return new TarArchiveInputStream( new GZIPInputStream( openMetaFileStream( metaFile ) ) );
    }

    @Override
    public void close()
        throws IOException
    {
        zipFile.close();
    }

    private boolean exists( final PathRef file )
    {
        return zipFile.getEntry( file.asString() ) != null;
    }

    private InputStream openMetaFileStream( final PathRef metaFile )
        throws IOException
    {
        final ZipArchiveEntry entry = zipFile.getEntry( metaFile.asString() );
        return zipFile.getInputStream( entry );
    }

    private ByteSource getBlobByteSource( RepositoryId repositoryId, SegmentLevel segmentLevel, BlobKey blobKey )
    {
        return new ZipDumpBlobRecord( RepositorySegmentUtils.toSegment( repositoryId, segmentLevel ), blobKey ).getBytes();
    }

    private Stream<String> listDirectories( final PathRef repoRootPath )
    {
        final String prefix = repoRootPath.asString() + "/";
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( zipFile.getEntries().asIterator(), Spliterator.ORDERED ), false )
            .map( ZipArchiveEntry::getName )
            .filter( name -> name.startsWith( prefix ) )
            .filter( name -> name.indexOf( '/', prefix.length() ) != -1 )
            .map( name -> name.substring( prefix.length(), name.indexOf( '/', prefix.length() ) ) )
            .distinct();
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

        @Override
        public com.google.common.base.Optional<Long> sizeIfKnown()
        {
            final long size = zipFile.getEntry( zipEntryName ).getSize();
            if ( size == ArchiveEntry.SIZE_UNKNOWN )
            {
                return com.google.common.base.Optional.absent();
            }
            else
            {
                return com.google.common.base.Optional.of( size );
            }
        }
    }

    class ZipDumpBlobRecord
        implements BlobRecord
    {
        private final BlobReference reference;

        private final ZipEntryByteSource zipEntryByteSource;

        public ZipDumpBlobRecord( final Segment segment, final BlobKey key )
        {
            this.reference = new BlobReference( segment, key );
            this.zipEntryByteSource =
                new ZipEntryByteSource( zipFile, DumpBlobStoreUtils.getBlobPathRef( basePathInZip, reference ).asString() );
        }

        @Override
        public BlobKey getKey()
        {
            return reference.key();
        }

        @Override
        public long getLength()
        {
            try
            {
                return this.zipEntryByteSource.size();
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        @Override
        public ByteSource getBytes()
        {
            return zipEntryByteSource;
        }

        @Override
        public long lastModified()
        {
            return zipEntryByteSource.zipFile.getEntry( zipEntryByteSource.zipEntryName ).getLastModifiedTime().toMillis();
        }
    }
}
