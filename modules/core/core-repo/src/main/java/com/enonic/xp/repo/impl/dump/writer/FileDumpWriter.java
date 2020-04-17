package com.enonic.xp.repo.impl.dump.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.blobstore.FileDumpBlobStore;

public class FileDumpWriter
    extends AbstractDumpWriter
{
    private Path dumpPath;

    private FileDumpWriter( final BlobStore blobStore, FilePaths filePaths, Path dumpPath, DumpBlobStore dumpBlobStore )
    {
        super( blobStore, filePaths, dumpBlobStore );
        this.dumpPath = dumpPath;
    }

    public static FileDumpWriter create( final Path basePath, final String dumpName, final BlobStore blobStore )
    {
        return create( basePath, dumpName, blobStore, new DefaultFilePaths() );
    }

    public static FileDumpWriter create( final Path basePath, final String dumpName, final BlobStore blobStore, final FilePaths filePaths )
    {
        final Path dumpPath = basePath.resolve( dumpName );
        return new FileDumpWriter( blobStore, filePaths, dumpPath, new FileDumpBlobStore( dumpPath ) );
    }

    protected OutputStream openMetaFileStream( final PathRef metaFile )
        throws IOException
    {
        final Path path = metaFile.asPath( dumpPath );
        Files.createDirectories( path.getParent() );
        return Files.newOutputStream( path );
    }
}
