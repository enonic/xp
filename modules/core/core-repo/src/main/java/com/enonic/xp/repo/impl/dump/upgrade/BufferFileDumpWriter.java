package com.enonic.xp.repo.impl.dump.upgrade;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.writer.FileDumpWriter;
import com.enonic.xp.repository.RepositoryId;

public class BufferFileDumpWriter
    extends FileDumpWriter
{
    private final String suffix;

    public BufferFileDumpWriter( final Path basePath, final String dumpName, final BlobStore blobStore, final String suffix )
    {
        super( basePath, dumpName, blobStore );
        this.suffix = suffix;
    }

    @Override
    protected Path createBranchMetaPath( final Path basePath, final RepositoryId repositoryId, final Branch branch )
    {
        return Paths.get( createBranchRootPath( basePath, repositoryId ).toString(), branch.toString(), "meta-" + suffix + ".tar.gz" );
    }

    @Override
    protected Path createVersionMetaPath( final Path basePath, final RepositoryId repositoryId )
    {
        return Paths.get( createBranchRootPath( basePath, repositoryId ).toString(), "versions-" + suffix + ".tar.gz" );
    }
}
