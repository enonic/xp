package com.enonic.xp.repo.impl.dump.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.AbstractFileProcessor;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public class FileDumpReader
    extends AbstractFileProcessor
    implements DumpReader
{
    private final Path dumpDirectory;

    private final BlobStore dumpBlobStore;

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

    private java.nio.file.Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
    }


    private DumpMeta readDumpMetaData()
    {
        final Path dumpMetaFile = Paths.get( this.dumpDirectory.toString(), "dump.json" );
        try
        {
            final String json = Files.toString( dumpMetaFile.toFile(), Charset.defaultCharset() );
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

        final List<RepositoryId> repositories = Lists.newArrayList();

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

        final List<Branch> branches = Lists.newArrayList();

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
        final File tarFile = getBranchEntriesFile( repositoryId, branch );

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
        final File versionsFile = getVersionsFile( repositoryId );

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

    private EntriesLoadResult doLoadEntries( final LineProcessor<EntryLoadResult> processor, final File tarFile )
    {
        final EntriesLoadResult.Builder result = EntriesLoadResult.create();

        try
        {
            final TarArchiveInputStream tarInputStream = openStream( tarFile );
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

    private TarArchiveInputStream openStream( final File tarFile )
        throws IOException
    {
        final FileInputStream fileInputStream = new FileInputStream( tarFile );
        final GZIPInputStream gzipInputStream = new GZIPInputStream( fileInputStream );
        return new TarArchiveInputStream( gzipInputStream );
    }

    private boolean isValidDumpDataDirectory( final Path folder )
    {
        final File file = folder.toFile();
        return file.exists() && file.isDirectory() && !file.isHidden();
    }

    private File getBranchEntriesFile( final RepositoryId repositoryId, final Branch branch )
    {
        final Path metaPath = createBranchMetaPath( this.dumpDirectory, repositoryId, branch );
        return doGetFile( metaPath );
    }

    private File getVersionsFile( final RepositoryId repositoryId )
    {
        final Path metaPath = createVersionMetaPath( this.dumpDirectory, repositoryId );
        return doGetFile( metaPath, false );
    }

    private File doGetFile( final Path metaPath )
    {
        return doGetFile( metaPath, true );
    }

    private File doGetFile( final Path metaPath, final boolean required )
    {
        final File tarFile = metaPath.toFile();

        if ( !tarFile.exists() )
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
        byte[] bytesToRead = new byte[1024];
        ByteArrayOutputStream entryAsByteStream = new ByteArrayOutputStream();
        int length;
        while ( ( length = tarInputStream.read( bytesToRead ) ) != -1 )
        {
            entryAsByteStream.write( bytesToRead, 0, length );
        }
        entryAsByteStream.close();

        return entryAsByteStream.toString( StandardCharsets.UTF_8.name() );
    }

    @Override
    public NodeVersion get( final NodeVersionId nodeVersionId )
    {
        final BlobRecord record =
            this.dumpBlobStore.getRecord( DumpConstants.DUMP_SEGMENT_NODES, BlobKey.from( nodeVersionId.toString() ) );

        if ( record == null )
        {
            throw new RepoLoadException( "Cannot find referred version id " + nodeVersionId + " in dump" );
        }

        return this.factory.create( record.getBytes() );
    }

    @Override
    public ByteSource getBinary( final String blobKey )
    {
        final BlobRecord record = this.dumpBlobStore.getRecord( DumpConstants.DUMP_SEGMENT_BINARIES, BlobKey.from( blobKey ) );

        if ( record == null )
        {
            throw new RepoLoadException( "Cannot find referred blob id " + blobKey + " in dump" );
        }

        return record.getBytes();
    }
}
