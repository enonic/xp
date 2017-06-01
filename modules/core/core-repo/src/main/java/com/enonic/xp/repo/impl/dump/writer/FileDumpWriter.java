package com.enonic.xp.repo.impl.dump.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.serializer.DumpEntrySerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpEntryJsonSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;

public class FileDumpWriter
    implements DumpWriter
{
    private final static Segment DUMP_SEGMENT_VERSION = Segment.from( "version" );

    private final static Segment DUMP_SEGMENT_BINARY = Segment.from( "binary" );

    private final static String LINE_SEPARATOR = System.lineSeparator();

    private final BlobStore dumpBlobStore;

    private final BlobStore blobStore;

    private final Path dumpDirectory;

    private final DumpEntrySerializer serializer;

    private BufferedWriter metaFileWriter;

    private FileDumpWriter( final Builder builder )
    {
        this.dumpDirectory = getDumpDirectory( builder.basePath, builder.dumpName );
        this.dumpBlobStore = new DumpBlobStore( this.dumpDirectory.toFile() );
        this.serializer = new DumpEntryJsonSerializer();
        this.blobStore = builder.blobStore;
    }

    private Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
    }

    @Override
    public void open( final RepositoryId repositoryId, final Branch branch )
    {
        try
        {
            final File metaFile = createMetaFile( repositoryId, branch );
            this.metaFileWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( metaFile, true ), "UTF-8" ) );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Could not open meta-file", e );
        }
    }

    private File createMetaFile( final RepositoryId repositoryId, final Branch branch )
    {
        final File metaFile = createMetaPath( repositoryId, branch ).toFile();

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

    private Path createMetaPath( final RepositoryId repositoryId, final Branch branch )
    {
        return Paths.get( this.dumpDirectory.toString(), repositoryId.toString(), branch.toString() );
    }

    @Override
    public void close()
    {
        if ( this.metaFileWriter == null )
        {
            return;
        }

        try
        {
            this.metaFileWriter.flush();
            this.metaFileWriter.close();
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Could not close meta-file", e );
        }
    }

    @Override
    public void writeMetaData( final DumpEntry dumpEntry )
    {
        try
        {
            metaFileWriter.write( serializer.serialize( dumpEntry ) + LINE_SEPARATOR );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Could not write dump-entry", e );
        }
    }

    @Override
    public void writeVersion( final NodeVersionId nodeVersionId )
    {
        final BlobRecord existingVersion = blobStore.getRecord( NodeConstants.NODE_SEGMENT, BlobKey.from( nodeVersionId.toString() ) );

        this.dumpBlobStore.addRecord( DUMP_SEGMENT_VERSION, existingVersion.getBytes() );
    }

    @Override
    public void writeBinary( final String blobKey )
    {
        final BlobRecord binaryRecord = blobStore.getRecord( NodeConstants.BINARY_SEGMENT, BlobKey.from( blobKey ) );

        this.dumpBlobStore.addRecord( DUMP_SEGMENT_BINARY, binaryRecord.getBytes() );
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
