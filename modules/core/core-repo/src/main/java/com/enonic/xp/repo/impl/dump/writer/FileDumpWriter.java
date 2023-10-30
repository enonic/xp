package com.enonic.xp.repo.impl.dump.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.base.Preconditions;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.blobstore.FileDumpBlobStore;

public class FileDumpWriter
    extends AbstractDumpWriter
{
    private final Path dumpPath;

    private FileDumpWriter( FilePaths filePaths, Path dumpPath, DumpBlobStore dumpBlobStore )
    {
        super( filePaths, dumpBlobStore );
        this.dumpPath = dumpPath;
    }

    public static FileDumpWriter create( final Path basePath, final String dumpName, final BlobStore sourceBlobStore )
    {
        return create( basePath, dumpName, sourceBlobStore, new DefaultFilePaths() );
    }

    public static FileDumpWriter create( final Path basePath, final String dumpName, final BlobStore sourceBlobStore,
                                         final FilePaths filePaths )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );

        final Path dumpPath = basePath.resolve( dumpName );
        return new FileDumpWriter( filePaths, dumpPath, new FileDumpBlobStore( dumpPath, sourceBlobStore ) );
    }

    @Override
    protected OutputStream openMetaFileStream( final PathRef metaFile )
        throws IOException
    {
        final Path path = metaFile.asPath( dumpPath );
        Files.createDirectories( path.getParent() );
        return Files.newOutputStream( path );
    }
}
