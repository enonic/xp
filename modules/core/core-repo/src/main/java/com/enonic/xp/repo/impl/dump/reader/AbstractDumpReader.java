package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.CommitsLoadResult;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.NullSystemLoadListener;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobRecord;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositorySegmentUtils;

public abstract class AbstractDumpReader
    implements DumpReader
{
    private final DumpBlobStore dumpBlobStore;

    private final NodeVersionFactory factory;

    private final SystemLoadListener listener;

    protected final FilePaths filePaths;

    protected AbstractDumpReader( final SystemLoadListener listener, FilePaths filePaths, DumpBlobStore dumpBlobStore )
    {
        this.listener = Objects.requireNonNullElseGet( listener, NullSystemLoadListener::new );
        this.dumpBlobStore = dumpBlobStore;
        this.factory = new NodeVersionFactory();
        this.filePaths = filePaths;
    }

    @Override
    public RepositoryIds getRepositories()
    {
        final PathRef repoRootPath = filePaths.repoRootPath();

        try (final Stream<String> stream = listDirectories( repoRootPath ))
        {
            return RepositoryIds.from( stream.map( RepositoryId::from ).collect( ImmutableSet.toImmutableSet() ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public Branches getBranches( final RepositoryId repositoryId )
    {
        final PathRef branchRootPath = filePaths.branchRootPath( repositoryId );

        try (final Stream<String> stream = listDirectories( branchRootPath ))
        {
            return Branches.from( stream.map( Branch::from ).collect( ImmutableSet.toImmutableSet() ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public BranchLoadResult loadBranch( final RepositoryId repositoryId, final Branch branch,
                                        final LineProcessor<EntryLoadResult> processor )
    {
        final PathRef tarFile = filePaths.branchMetaPath( repositoryId, branch );

        listener.loadingBranch( repositoryId, branch, getBranchSuccessfulCountFromMeta( repositoryId, branch ) );

        final BranchLoadResult.Builder builder = BranchLoadResult.create( branch );

        if ( !exists( tarFile ) )
        {
            return builder.build();
        }

        final EntriesLoadResult result = doLoadEntries( processor, tarFile );

        return builder.
            successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) ).
            build();
    }

    @Override
    public VersionsLoadResult loadVersions( final RepositoryId repositoryId, final LineProcessor<EntryLoadResult> processor )
    {
        final PathRef tarFile = filePaths.versionMetaPath( repositoryId );

        listener.loadingVersions( repositoryId );

        final VersionsLoadResult.Builder builder = VersionsLoadResult.create();

        if ( !exists( tarFile ) )
        {
            return builder.build();
        }

        final EntriesLoadResult result = doLoadEntries( processor, tarFile );

        return builder.
            successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) ).
            build();
    }

    @Override
    public CommitsLoadResult loadCommits( final RepositoryId repositoryId, final LineProcessor<EntryLoadResult> processor )
    {
        final PathRef tarFile = filePaths.commitMetaPath( repositoryId );

        listener.loadingCommits( repositoryId );

        final CommitsLoadResult.Builder builder = CommitsLoadResult.create();

        if ( !exists( tarFile ) )
        {
            return builder.build();
        }

        final EntriesLoadResult result = doLoadEntries( processor, tarFile );

        return builder.
            successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) ).
            build();
    }

    @Override
    public NodeVersion get( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_NODE_SEGMENT_LEVEL );
        final DumpBlobRecord dataRecord = this.dumpBlobStore.getRecord( nodeSegment, nodeVersionKey.getNodeBlobKey() );

        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_INDEX_CONFIG_SEGMENT_LEVEL );
        final DumpBlobRecord indexConfigRecord = this.dumpBlobStore.getRecord( indexConfigSegment, nodeVersionKey.getIndexConfigBlobKey() );

        final Segment accessControlSegment =
            RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_ACCESS_CONTROL_SEGMENT_LEVEL );
        final DumpBlobRecord accessControlRecord =
            this.dumpBlobStore.getRecord( accessControlSegment, nodeVersionKey.getAccessControlBlobKey() );

        return this.factory.create( dataRecord.getBytes(), indexConfigRecord.getBytes(), accessControlRecord.getBytes() );
    }

    @Override
    public ByteSource getBinary( final RepositoryId repositoryId, final String blobKey )
    {
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_BINARY_SEGMENT_LEVEL );
        final DumpBlobRecord record = this.dumpBlobStore.getRecord( segment, BlobKey.from( blobKey ) );

        if ( record == null )
        {
            throw new RepoLoadException( "Cannot find referred blob id " + blobKey + " in dump" );
        }

        return record.getBytes();
    }

    @Override
    public DumpMeta getDumpMeta()
    {
        return readDumpMetaData();
    }

    @Override
    public void close()
        throws IOException
    {
    }

    protected abstract InputStream openMetaFileStream( final PathRef metaFile )
        throws IOException;

    protected abstract Stream<String> listDirectories( final PathRef repoRootPath )
        throws IOException;

    protected abstract boolean exists( final PathRef file );

    private DumpMeta readDumpMetaData()
    {
        try (final InputStream stream = openMetaFileStream( filePaths.metaDataFile() ))
        {
            return new DumpMetaJsonSerializer().toDumpMeta( new String( stream.readAllBytes(), StandardCharsets.UTF_8 ) );
        }
        catch ( IOException e )
        {
            throw new RepoLoadException( "Cannot read dump-meta file", e );
        }
    }

    private EntriesLoadResult doLoadEntries( final LineProcessor<EntryLoadResult> processor, final PathRef tarFile )
    {
        final EntriesLoadResult.Builder result = EntriesLoadResult.create();

        try (final TarArchiveInputStream tarInputStream = openStream( tarFile ))
        {
            TarArchiveEntry entry = tarInputStream.getNextTarEntry();
            while ( entry != null )
            {
                final EntryLoadResult entryLoadResult = handleEntry( processor, tarInputStream );
                result.add( entryLoadResult );
                entry = tarInputStream.getNextTarEntry();
            }
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read meta-data", e );
        }

        return result.build();
    }

    private TarArchiveInputStream openStream( final PathRef metaFile )
        throws IOException
    {
        return new TarArchiveInputStream( new GZIPInputStream( openMetaFileStream( metaFile ) ) );
    }

    private EntryLoadResult handleEntry( final LineProcessor<EntryLoadResult> processor, final TarArchiveInputStream tarInputStream )
        throws IOException
    {
        String content = readEntry( tarInputStream );

        processor.processLine( content );

        this.listener.entryLoaded();

        return processor.getResult();
    }

    private String readEntry( final TarArchiveInputStream tarInputStream )
        throws IOException
    {
        return new String( tarInputStream.readAllBytes(), StandardCharsets.UTF_8 );
    }

    private Long getBranchSuccessfulCountFromMeta( final RepositoryId repositoryId, final Branch branch )
    {
        final SystemDumpResult systemDumpResult = readDumpMetaData().getSystemDumpResult();
        if ( systemDumpResult != null )
        {
            final RepoDumpResult repoDumpResult = systemDumpResult.get( repositoryId );
            if ( repoDumpResult != null )
            {
                final BranchDumpResult branchDumpResult = repoDumpResult.get( branch );
                if ( branchDumpResult != null )
                {
                    return branchDumpResult.getSuccessful();
                }
            }
        }
        return null;
    }
}
