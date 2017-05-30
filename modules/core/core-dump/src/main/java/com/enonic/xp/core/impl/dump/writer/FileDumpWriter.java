package com.enonic.xp.core.impl.dump.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.impl.dump.DumpBlobStore;
import com.enonic.xp.core.impl.dump.RepoDumpException;
import com.enonic.xp.core.impl.dump.model.DumpEntry;
import com.enonic.xp.core.impl.dump.serializer.DumpEntrySerializer;
import com.enonic.xp.core.impl.dump.serializer.json.DumpEntryJsonSerializer;
import com.enonic.xp.repository.RepositoryId;

public class FileDumpWriter
    implements DumpWriter
{
    private final static String LINE_SEPARATOR = System.lineSeparator();

    private final BlobStore dumpBlobStore;

    private final static Segment version = Segment.from( "version" );

    private final static Segment BINARY = Segment.from( "binary" );

    private final Path dumpDirectory;

    private final DumpEntrySerializer serializer;

    private BufferedWriter metaFileWriter;

    public FileDumpWriter( final Path basePath, final String dumpName )
    {
        this.dumpDirectory = getDumpDirectory( basePath, dumpName );
        this.dumpBlobStore = new DumpBlobStore( this.dumpDirectory.toFile() );
        this.serializer = new DumpEntryJsonSerializer();
    }

    private java.nio.file.Path getDumpDirectory( final Path basePath, final String name )
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
    public void write( final DumpEntry dumpEntry )
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
    public void writeVersion( final BlobKey blobKey, final ByteSource source )
    {
        this.dumpBlobStore.addRecord( version, source );
    }


    @Override
    public void writeBinary( final BlobKey blobKey, final ByteSource source )
    {
        this.dumpBlobStore.addRecord( BINARY, source );
    }

}
