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
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.AbstractFileProcessor;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repository.RepositoryId;

public class FileDumpReader
    extends AbstractFileProcessor
    implements DumpReader
{
    private final Path dumpDirectory;

    private final BlobStore dumpBlobStore;

    private final NodeVersionFactory factory;

    public FileDumpReader( final Path basePath, final String dumpName )
    {
        this.dumpDirectory = getDumpDirectory( basePath, dumpName );
        this.dumpBlobStore = new DumpBlobStore( this.dumpDirectory.toFile() );
        this.factory = new NodeVersionFactory();
    }

    private java.nio.file.Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
    }

    @Override
    public Branches getBranches( final RepositoryId repositoryId )
    {
        final Path repoPath = createRepoDumpPath( this.dumpDirectory, repositoryId );

        if ( !repoPath.toFile().exists() )
        {
            throw new RepoDumpException( String.format( "Repository %s does not exist in dump %s", repositoryId, this.dumpDirectory ) );
        }

        final String[] branchFiles = repoPath.toFile().list();

        final List<Branch> branches = Lists.newArrayList();

        for ( final String branch : branchFiles )
        {
            branches.add( Branch.from( branch ) );
        }

        return Branches.from( branches );
    }

    @Override
    public void load( final RepositoryId repositoryId, final Branch branch, final LineProcessor<EntryLoadResult> processor )
    {

        final Path metaPath = createMetaPath( this.dumpDirectory, repositoryId, branch );
        final File tarFile = metaPath.toFile();

        if ( !tarFile.exists() )
        {
            throw new RepoDumpException( "File doesnt " + metaPath + " exists" );
        }

        try
        {
            final FileInputStream fileInputStream = new FileInputStream( tarFile );
            final GZIPInputStream gzipInputStream = new GZIPInputStream( fileInputStream );
            final TarArchiveInputStream tarInputStream = new TarArchiveInputStream( gzipInputStream );

            TarArchiveEntry entry = tarInputStream.getNextTarEntry();

            while ( entry != null )
            {
                String content = readEntry( tarInputStream );
                processor.processLine( content );
                entry = tarInputStream.getNextTarEntry();
            }
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read meta-data", e );
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

    @Override
    public NodeVersion get( final NodeVersionId nodeVersionId )
    {
        final BlobRecord record =
            this.dumpBlobStore.getRecord( DumpConstants.DUMP_SEGMENT_NODES, BlobKey.from( nodeVersionId.toString() ) );

        if ( record == null )
        {
            throw new RepoDumpException( "Cannot find referred version id " + nodeVersionId + " in dump" );
        }

        return this.factory.create( record.getBytes() );
    }

    @Override
    public ByteSource getBinary( final String blobKey )
    {
        final BlobRecord record = this.dumpBlobStore.getRecord( DumpConstants.DUMP_SEGMENT_BINARIES, BlobKey.from( blobKey ) );

        if ( record == null )
        {
            throw new RepoDumpException( "Cannot find referred blob id " + blobKey + " in dump" );
        }

        return record.getBytes();
    }
}
