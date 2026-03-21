package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

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
import com.enonic.xp.repo.impl.dump.FileUtils;
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

public class FileDumpReaderV7
    implements DumpReaderV7
{
    private final Path dumpPath;

    private final FilePaths filePaths;

    private FileDumpReaderV7( final FilePaths filePaths, final Path dumpPath )
    {
        this.filePaths = filePaths;
        this.dumpPath = dumpPath;
    }

    public static FileDumpReaderV7 create( Path basePath, final String dumpName )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );

        final Path dumpPath = basePath.resolve( dumpName );
        if ( !FileUtils.isVisibleDirectory( dumpPath ) )
        {
            throw new RepoLoadException( "Directory is not a valid dump directory: [" + dumpPath + "]" );
        }
        return new FileDumpReaderV7( new DefaultFilePaths(), dumpPath );
    }

    public RepositoryIds getRepositories()
    {
        final PathRef repoRootPath = filePaths.repoRootPath();

        try (Stream<String> stream = listDirectories( repoRootPath ))
        {
            return stream.map( RepositoryId::from ).collect( RepositoryIds.collector() );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public Branches getBranches( final RepositoryId repositoryId )
    {
        final PathRef branchRootPath = filePaths.branchRootPath( repositoryId );

        try (Stream<String> stream = listDirectories( branchRootPath ))
        {
            return stream.map( Branch::from ).collect( Branches.collector() );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
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
            try (InputStream stream = Files.newInputStream( filePaths.metaDataFile().asPath( dumpPath ) ))
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
        return new FileDumpBlobRecord( segment, key );
    }

    public Optional<PathRef> getBranchEntries( final RepositoryId repositoryId, final Branch branch )
    {
        final PathRef pathRef = filePaths.branchMetaPath( repositoryId, branch );
        return pathRef.asPath( dumpPath ).toFile().exists() ? Optional.of( pathRef ) : Optional.empty();
    }

    public Optional<PathRef> getVersions( final RepositoryId repositoryId )
    {
        final PathRef pathRef = filePaths.versionMetaPath( repositoryId );
        return pathRef.asPath( dumpPath ).toFile().exists() ? Optional.of( pathRef ) : Optional.empty();
    }

    public Optional<PathRef> getCommits( final RepositoryId repositoryId )
    {
        final PathRef pathRef = filePaths.commitMetaPath( repositoryId );
        return pathRef.asPath( dumpPath ).toFile().exists() ? Optional.of( pathRef ) : Optional.empty();
    }

    public TarArchiveInputStream openTarStream( final PathRef metaFile )
        throws IOException
    {
        return new TarArchiveInputStream( new GZIPInputStream( Files.newInputStream( metaFile.asPath( dumpPath ) ) ) );
    }

    @Override
    public void close()
    {
    }

    private ByteSource getBlobByteSource( RepositoryId repositoryId, SegmentLevel segmentLevel, BlobKey blobKey )
    {
        return MoreFiles.asByteSource(
            getBlobByteSource( new BlobReference( RepositorySegmentUtils.toSegment( repositoryId, segmentLevel ), blobKey ) ) );
    }

    private Path getBlobByteSource( BlobReference blobReference )
    {
        return DumpBlobStoreUtils.getBlobPathRef( PathRef.of(), blobReference ).asPath( dumpPath );
    }

    private Stream<String> listDirectories( final PathRef repoRootPath )
        throws IOException
    {
        return Files.list( repoRootPath.asPath( dumpPath ) )
            .filter( FileUtils::isVisibleDirectory )
            .map( dir -> dir.getFileName().toString() );
    }

    class FileDumpBlobRecord
        implements BlobRecord
    {
        private final BlobReference reference;

        private final Path blobPath;

        public FileDumpBlobRecord( final Segment segment, final BlobKey key )
        {
            this.reference = new BlobReference( segment, key );
            this.blobPath = DumpBlobStoreUtils.getBlobPathRef( PathRef.of(), reference ).asPath( dumpPath );
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
                return Files.size( blobPath );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        @Override
        public ByteSource getBytes()
        {
            return MoreFiles.asByteSource( blobPath );
        }

        @Override
        public long lastModified()
        {
            try
            {
                return Files.getLastModifiedTime( blobPath ).toMillis();
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }
    }
}
