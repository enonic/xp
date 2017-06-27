package com.enonic.xp.repo.impl.dump.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.AbstractFileProcessor;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
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

    public FileDumpReader( final Path basePath, final String dumpName, final SystemLoadListener listener )
    {
        this.dumpDirectory = getDumpDirectory( basePath, dumpName );

        if ( !this.dumpDirectory.toFile().exists() )
        {
            throw new RepoDumpException( "Dump directory does not exist: [" + this.dumpDirectory + "]" );
        }

        this.listener = listener;
        this.dumpBlobStore = new DumpBlobStore( this.dumpDirectory.toFile() );
        this.factory = new NodeVersionFactory();
    }

    private java.nio.file.Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
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
            repositories.add( RepositoryId.from( repoId ) );
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
            branches.add( Branch.from( branch ) );
        }

        return Branches.from( branches );
    }

    @Override
    public BranchLoadResult load( final RepositoryId repositoryId, final Branch branch, final LineProcessor<EntryLoadResult> processor )
    {
        final BranchLoadResult.Builder result = BranchLoadResult.create( branch );
        final File tarFile = getDumpFile( repositoryId, branch );

        if ( this.listener != null )
        {
            this.listener.loadingBranch( repositoryId, branch );
        }

        try
        {
            final FileInputStream fileInputStream = new FileInputStream( tarFile );
            final GZIPInputStream gzipInputStream = new GZIPInputStream( fileInputStream );
            final TarArchiveInputStream tarInputStream = new TarArchiveInputStream( gzipInputStream );

            TarArchiveEntry entry = tarInputStream.getNextTarEntry();

            while ( entry != null )
            {
                handleEntry( processor, result, tarInputStream );
                entry = tarInputStream.getNextTarEntry();
            }

            return result.build();
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read meta-data", e );
        }
    }

    private File getDumpFile( final RepositoryId repositoryId, final Branch branch )
    {
        final Path metaPath = createMetaPath( this.dumpDirectory, repositoryId, branch );
        final File tarFile = metaPath.toFile();

        if ( !tarFile.exists() )
        {
            throw new RepoDumpException( "File doesnt " + metaPath + " exists" );
        }
        return tarFile;
    }

    private void handleEntry( final LineProcessor<EntryLoadResult> processor, final BranchLoadResult.Builder result,
                              final TarArchiveInputStream tarInputStream )
        throws IOException
    {
        String content = readEntry( tarInputStream );
        processor.processLine( content );
        reportEntry( processor, result );

        if ( this.listener != null )
        {
            this.listener.nodeLoaded();
        }
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

    private void reportEntry( final LineProcessor<EntryLoadResult> processor, final BranchLoadResult.Builder result )
    {
        final EntryLoadResult entryResult = processor.getResult();
        result.addedNode();
        result.addedVersions( entryResult.getVersions() );
        entryResult.getErrors().forEach( ( error ) -> result.error( LoadError.error( error.getMessage() ) ) );
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
