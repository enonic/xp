package com.enonic.xp.core.impl.dump;

import java.io.File;
import java.nio.file.Path;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryId;

public class RepoLoader
{
    private final static String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private final RepositoryId repositoryId;

    private final Branches branches;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final Path sourcePath;

    private RepoLoader( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        branches = builder.branches;
        includeVersions = builder.includeVersions;
        includeBinaries = builder.includeBinaries;
        sourcePath = builder.sourcePath;
    }

    public LoadResult execute()
    {
        final File sourceDir = getSourceDir();


        return null;
    }

    private File getSourceDir()
    {
        final File sourceDir = sourcePath.toFile();

        if ( !sourceDir.exists() )
        {
            throw new RepoDumpException( "Could not open source [" + sourcePath + "], not found" );
        }

        if ( !sourceDir.isDirectory() )
        {
            throw new RepoDumpException( "Could not open source [" + sourcePath + "], not a directory" );
        }
        return sourceDir;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private Branches branches;

        private boolean includeVersions;

        private boolean includeBinaries;

        private Path sourcePath;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder branches( final Branches val )
        {
            branches = val;
            return this;
        }

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public Builder includeBinaries( final boolean val )
        {
            includeBinaries = val;
            return this;
        }

        public Builder sourcePath( final Path val )
        {
            sourcePath = val;
            return this;
        }

        public RepoLoader build()
        {
            return new RepoLoader( this );
        }
    }
}
