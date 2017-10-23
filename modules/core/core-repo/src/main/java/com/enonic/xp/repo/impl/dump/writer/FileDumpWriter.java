package com.enonic.xp.repo.impl.dump.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.AbstractFileProcessor;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.serializer.DumpSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;

public class FileDumpWriter
    extends AbstractFileProcessor
    implements DumpWriter
{
    private final static Logger LOG = LoggerFactory.getLogger( FileDumpWriter.class );

    private final BlobStore dumpBlobStore;

    private final BlobStore blobStore;

    private final Path dumpDirectory;

    private final DumpSerializer serializer;

    private GZIPOutputStream gzipOut;

    private FileOutputStream fileOut;

    private TarArchiveOutputStream tarOutputStream;

    private FileDumpWriter( final Builder builder )
    {
        this.dumpDirectory = getDumpDirectory( builder.basePath, builder.dumpName );
        this.dumpBlobStore = new DumpBlobStore( this.dumpDirectory.toFile() );
        this.serializer = new JsonDumpSerializer();
        this.blobStore = builder.blobStore;
    }

    private Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
    }

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        final Path dumpMetaFile = Paths.get( this.dumpDirectory.toString(), "dump.json" );

        final String serialized = new DumpMetaJsonSerializer().serialize( dumpMeta );

        try
        {
            Files.write( serialized, dumpMetaFile.toFile(), Charset.defaultCharset() );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot write dump-meta file", e );
        }
    }

    @Override
    public void openBranchMeta( final RepositoryId repositoryId, final Branch branch )
    {
        try
        {
            final File metaFile = createBranchMeta( repositoryId, branch );
            doOpenFile( metaFile );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Could not open meta-file", e );
        }
    }

    @Override
    public void openVersionsMeta( final RepositoryId repositoryId )
    {
        try
        {
            final File metaFile = createVersionsMeta( repositoryId );
            doOpenFile( metaFile );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Could not open meta-file", e );
        }
    }

    private void doOpenFile( final File metaFile )
        throws IOException
    {
        this.fileOut = new FileOutputStream( metaFile );
        this.gzipOut = new GZIPOutputStream( fileOut );
        this.tarOutputStream = new TarArchiveOutputStream( gzipOut );
        this.tarOutputStream.setLongFileMode( TarArchiveOutputStream.LONGFILE_POSIX );
    }

    private File createBranchMeta( final RepositoryId repositoryId, final Branch branch )
    {
        final File metaFile = createBranchMetaPath( this.dumpDirectory, repositoryId, branch ).toFile();

        return doCreateMetaFile( metaFile );
    }

    private File createVersionsMeta( final RepositoryId repositoryId )
    {
        final File metaFile = createVersionMetaPath( this.dumpDirectory, repositoryId ).toFile();

        return doCreateMetaFile( metaFile );
    }

    private File doCreateMetaFile( final File metaFile )
    {
        if ( metaFile.exists() )
        {
            throw new RepoDumpException( "Meta-file with path [" + metaFile.getPath() + "] already exists" );
        }

        metaFile.getParentFile().mkdirs();

        if ( !metaFile.getParentFile().exists() )
        {
            throw new RepoDumpException( "Not able to create parent-directory [" + metaFile.getParentFile().getPath() + "]" );
        }

        return metaFile;
    }

    @Override
    public void writeBranchEntry( final BranchDumpEntry branchDumpEntry )
    {
        final String serializedEntry = serializer.serialize( branchDumpEntry );
        final String entryName = branchDumpEntry.getNodeId().toString() + ".json";
        storeTarEntry( serializedEntry, entryName );
    }


    @Override
    public void writeVersionsEntry( final VersionsDumpEntry versionsDumpEntry )
    {
        final String serializedEntry = serializer.serialize( versionsDumpEntry );
        final String entryName = versionsDumpEntry.getNodeId().toString() + ".json";
        storeTarEntry( serializedEntry, entryName );
    }

    private void storeTarEntry( final String serializedEntry, final String entryName )
    {
        try
        {
            final byte[] data = serializedEntry.getBytes( Charsets.UTF_8 );
            final TarArchiveEntry entry = new TarArchiveEntry( entryName );
            entry.setSize( data.length );
            tarOutputStream.putArchiveEntry( entry );
            tarOutputStream.write( data );
            tarOutputStream.closeArchiveEntry();
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Could not write dump-entry", e );
        }
    }


    @Override
    public void writeVersionBlob( final NodeVersionId nodeVersionId )
    {
        final BlobRecord existingVersion =
            blobStore.getRecord( DumpConstants.DUMP_SEGMENT_NODES, BlobKey.from( nodeVersionId.toString() ) );

        if ( existingVersion == null )
        {
            throw new RepoDumpException( "Cannot write node version with key [" + nodeVersionId + "], not found in blobStore" );
        }

        this.dumpBlobStore.addRecord( NodeConstants.NODE_SEGMENT, existingVersion.getBytes() );
    }

    @Override
    public void writeBinaryBlob( final String blobKey )
    {
        final BlobRecord binaryRecord = blobStore.getRecord( DumpConstants.DUMP_SEGMENT_BINARIES, BlobKey.from( blobKey ) );

        if ( binaryRecord == null )
        {
            throw new RepoDumpException( "Cannot write binary with key [" + blobKey + "], not found in blobStore" );
        }

        this.dumpBlobStore.addRecord( NodeConstants.BINARY_SEGMENT, binaryRecord.getBytes() );
    }

    @Override
    public void close()
    {
        closeStream( this.tarOutputStream );
        closeStream( this.gzipOut );
        closeStream( this.fileOut );
    }

    private void closeStream( final OutputStream stream )
    {
        if ( stream == null )
        {
            return;
        }

        try
        {
            stream.flush();
            stream.close();
        }
        catch ( IOException e )
        {
            LOG.warn( "Cannot close stream [" + stream.getClass().getName() + "]", e );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Path basePath;

        private String dumpName;

        private BlobStore blobStore;

        private Builder()
        {
        }

        public Builder basePath( final Path basePath )
        {
            this.basePath = basePath;
            return this;
        }

        public Builder dumpName( final String dumpName )
        {
            this.dumpName = dumpName;
            return this;
        }

        public Builder blobStore( final BlobStore val )
        {
            blobStore = val;
            return this;
        }

        public FileDumpWriter build()
        {
            return new FileDumpWriter( this );
        }
    }
}
