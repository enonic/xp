package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
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
import com.enonic.xp.repo.impl.dump.AbstractFileProcessor;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositorySegmentUtils;

public class FileDumpReader
    extends AbstractFileProcessor
    implements DumpReader
{
    private final Path dumpDirectory;

    private final DumpBlobStore dumpBlobStore;

    private final NodeVersionFactory factory;

    private final SystemLoadListener listener;

    private final DumpMeta dumpMeta;

    public FileDumpReader( final Path basePath, final String dumpName, final SystemLoadListener listener )
    {
        this.dumpDirectory = getDumpDirectory( basePath, dumpName );

        if ( !isValidDumpDataDirectory( dumpDirectory ) )
        {
            throw new RepoLoadException( "Directory is not a valid dump directory: [" + this.dumpDirectory + "]" );
        }

        this.listener = listener;
        this.dumpBlobStore = new DumpBlobStore( this.dumpDirectory.toFile() );
        this.factory = new NodeVersionFactory();
        this.dumpMeta = readDumpMetaData();
    }

    public DumpBlobStore getDumpBlobStore()
    {
        return dumpBlobStore;
    }

    private java.nio.file.Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
    }


    private DumpMeta readDumpMetaData()
    {
        try
        {
            final String json = Files.readString( getMetaDataFile() );
            return new DumpMetaJsonSerializer().toDumpMeta( json );
        }
        catch ( IOException e )
        {
            throw new RepoLoadException( "Cannot read dump-meta file", e );
        }
    }

    @Override
    public RepositoryIds getRepositories()
    {
        final Path repoRootPath = createRepoRootPath( this.dumpDirectory );

        if ( !repoRootPath.toFile().exists() )
        {
            throw new RepoDumpException( String.format( "Folder 'meta' does not exist in dump directory %s", this.dumpDirectory ) );
        }

        final String[] repoIds = repoRootPath.toFile().list();

        final List<RepositoryId> repositories = new ArrayList<>();

        for ( final String repoId : repoIds )
        {
            final Path repoFolder = Paths.get( repoRootPath.toString(), repoId );
            if ( isValidDumpDataDirectory( repoFolder ) )
            {
                repositories.add( RepositoryId.from( repoId ) );
            }
        }

        return RepositoryIds.from( repositories );
    }


    @Override
    public Branches getBranches( final RepositoryId repositoryId )
    {
        final Path branchRootPath = createBranchRootPath( this.dumpDirectory, repositoryId );

        if ( !branchRootPath.toFile().exists() )
        {
            throw new RepoDumpException( String.format( "Repository %s does not exist in dump %s", repositoryId, this.dumpDirectory ) );
        }

        final String[] branchFiles = branchRootPath.toFile().list();

        final List<Branch> branches = new ArrayList<>();

        for ( final String branch : branchFiles )
        {
            final Path branchFolder = Paths.get( branchRootPath.toString(), branch );
            if ( isValidDumpDataDirectory( branchFolder ) )
            {
                branches.add( Branch.from( branch ) );
            }
        }

        return Branches.from( branches );
    }

    @Override
    public BranchLoadResult loadBranch( final RepositoryId repositoryId, final Branch branch,
                                        final LineProcessor<EntryLoadResult> processor )
    {
        final Path tarFile = getBranchEntriesFile( repositoryId, branch );

        if ( this.listener != null )
        {
            this.listener.loadingBranch( repositoryId, branch, getBranchSuccessfulCountFromMeta( repositoryId, branch ) );
        }

        final EntriesLoadResult result = doLoadEntries( processor, tarFile );

        return BranchLoadResult.create( branch ).
            successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) ).
            build();
    }

    @Override
    public VersionsLoadResult loadVersions( final RepositoryId repositoryId, final LineProcessor<EntryLoadResult> processor )
    {
        final Path versionsFile = getVersionsFile( repositoryId );

        if ( this.listener != null )
        {
            this.listener.loadingVersions( repositoryId );
        }

        final VersionsLoadResult.Builder builder = VersionsLoadResult.create();

        if ( versionsFile == null )
        {
            return builder.build();
        }

        final EntriesLoadResult result = doLoadEntries( processor, versionsFile );
        return builder.successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) ).
            build();
    }

    @Override
    public CommitsLoadResult loadCommits( final RepositoryId repositoryId, final LineProcessor<EntryLoadResult> processor )
    {
        final Path commitsFile = getCommitsFile( repositoryId );

        if ( this.listener != null )
        {
            this.listener.loadingCommits( repositoryId );
        }

        final CommitsLoadResult.Builder commitsLoadResult = CommitsLoadResult.create();

        if ( commitsFile == null )
        {
            return commitsLoadResult.build();
        }

        final EntriesLoadResult result = doLoadEntries( processor, commitsFile );
        return commitsLoadResult.successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) ).
            build();
    }

    private Long getBranchSuccessfulCountFromMeta( final RepositoryId repositoryId, final Branch branch )
    {
        final SystemDumpResult systemDumpResult = this.dumpMeta.getSystemDumpResult();
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

    private EntriesLoadResult doLoadEntries( final LineProcessor<EntryLoadResult> processor, final Path tarFile )
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

    public void processEntries( final BiConsumer<String, String> processor, final Path tarFile )
    {
        try (final TarArchiveInputStream tarInputStream = openStream( tarFile ))
        {
            TarArchiveEntry entry = tarInputStream.getNextTarEntry();
            while ( entry != null )
            {
                String entryContent = readEntry( tarInputStream );
                processor.accept( entryContent, entry.getName() );
                entry = tarInputStream.getNextTarEntry();
            }
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read meta-data", e );
        }
    }

    private TarArchiveInputStream openStream( final Path tarFile )
        throws IOException
    {
        final InputStream fileInputStream = Files.newInputStream( tarFile );
        final GZIPInputStream gzipInputStream = new GZIPInputStream( fileInputStream );
        return new TarArchiveInputStream( gzipInputStream );
    }

    private boolean isValidDumpDataDirectory( final Path folder )
    {
        return Files.exists( folder ) && Files.isDirectory( folder ) && !folder.toFile().isHidden(); // see JDK-8215467
    }

    public Path getBranchEntriesFile( final RepositoryId repositoryId, final Branch branch )
    {
        final Path metaPath = createBranchMetaPath( this.dumpDirectory, repositoryId, branch );
        return doGetFile( metaPath, false );
    }

    public Path getVersionsFile( final RepositoryId repositoryId )
    {
        final Path metaPath = createVersionMetaPath( this.dumpDirectory, repositoryId );
        return doGetFile( metaPath, false );
    }

    public Path getCommitsFile( final RepositoryId repositoryId )
    {
        final Path metaPath = createCommitMetaPath( this.dumpDirectory, repositoryId );
        return doGetFile( metaPath, false );
    }

    public Path getRepositoryDir( final RepositoryId repositoryId )
    {
        final Path repoPath = createRepoPath( this.dumpDirectory, repositoryId );
        return doGetFile( repoPath, false );
    }

    public Path getMetaDataFile()
    {
        return this.dumpDirectory.resolve( "dump.json" );
    }

    private Path doGetFile( final Path metaPath )
    {
        return doGetFile( metaPath, true );
    }

    private Path doGetFile( final Path metaPath, final boolean required )
    {
        final Path tarFile = metaPath;

        if ( !Files.exists( tarFile ) )
        {
            if ( !required )
            {
                return null;
            }

            throw new RepoDumpException( "File doesnt " + metaPath + " exists" );
        }
        return tarFile;
    }

    private EntryLoadResult handleEntry( final LineProcessor<EntryLoadResult> processor, final TarArchiveInputStream tarInputStream )
        throws IOException
    {
        String content = readEntry( tarInputStream );

        processor.processLine( content );

        if ( this.listener != null )
        {
            this.listener.entryLoaded();
        }

        return processor.getResult();
    }

    private String readEntry( final TarArchiveInputStream tarInputStream )
        throws IOException
    {
        return new String( tarInputStream.readAllBytes(), StandardCharsets.UTF_8 );
    }

    @Override
    public NodeVersion get( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_NODE_SEGMENT_LEVEL );
        final BlobRecord dataRecord = this.dumpBlobStore.getRecord( nodeSegment, nodeVersionKey.getNodeBlobKey() );
        if ( dataRecord == null )
        {
            throw new RepoLoadException( "Cannot find referred node blob " + nodeVersionKey.getNodeBlobKey() + " in dump" );
        }

        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_INDEX_CONFIG_SEGMENT_LEVEL );
        final BlobRecord indexConfigRecord = this.dumpBlobStore.getRecord( indexConfigSegment, nodeVersionKey.getIndexConfigBlobKey() );
        if ( indexConfigRecord == null )
        {
            throw new RepoLoadException( "Cannot find referred index config blob " + nodeVersionKey.getIndexConfigBlobKey() + " in dump" );
        }

        final Segment accessControlSegment =
            RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_ACCESS_CONTROL_SEGMENT_LEVEL );
        final BlobRecord accessControlRecord =
            this.dumpBlobStore.getRecord( accessControlSegment, nodeVersionKey.getAccessControlBlobKey() );
        if ( accessControlRecord == null )
        {
            throw new RepoLoadException(
                "Cannot find referred access control blob " + nodeVersionKey.getAccessControlBlobKey() + " in dump" );
        }

        return this.factory.create( dataRecord.getBytes(), indexConfigRecord.getBytes(), accessControlRecord.getBytes() );
    }

    @Override
    public ByteSource getBinary( final RepositoryId repositoryId, final String blobKey )
    {
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_BINARY_SEGMENT_LEVEL );
        final BlobRecord record = this.dumpBlobStore.getRecord( segment, BlobKey.from( blobKey ) );

        if ( record == null )
        {
            throw new RepoLoadException( "Cannot find referred blob id " + blobKey + " in dump" );
        }

        return record.getBytes();
    }

    @Override
    public DumpMeta getDumpMeta()
    {
        return this.dumpMeta;
    }
}
