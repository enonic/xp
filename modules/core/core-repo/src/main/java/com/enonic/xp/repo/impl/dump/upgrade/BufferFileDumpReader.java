package com.enonic.xp.repo.impl.dump.upgrade;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repository.RepositoryId;

public class BufferFileDumpReader
    extends FileDumpReader
{
    private final String suffix;

    public BufferFileDumpReader( final Path basePath, final String dumpName, final SystemLoadListener listener, final String suffix )
    {
        super( basePath, dumpName, listener );
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
