package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.RepositoryId;

public class IndexNameResolver
{
    private final static String SEARCH_INDEX_PREFIX = "search";

    private final static String VERSION_INDEX_PREFIX = "version";

    private final static String BRANCH_INDEX_PREFIX = "branch";

    private final static String COMMIT_INDEX_PREFIX = "commit";

    private final static String DIVIDER = "-";

    public static String resolveIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        if ( repositoryId == null || indexType == null )
        {
            return null;
        }

        switch ( indexType )
        {
            case SEARCH:
                return IndexNameResolver.resolveSearchIndexName( repositoryId );
            case VERSION:
                return IndexNameResolver.resolveVersionIndexName( repositoryId );
            case BRANCH:
                return IndexNameResolver.resolveBranchIndexName( repositoryId );
            default:
                return IndexNameResolver.resolveCommitIndexName( repositoryId );
        }
    }


    public static String resolveVersionIndexName( final RepositoryId repositoryId )
    {
        return VERSION_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    public static String resolveBranchIndexName( final RepositoryId repositoryId )
    {
        return BRANCH_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    public static String resolveCommitIndexName( final RepositoryId repositoryId )
    {
        return COMMIT_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    public static String resolveSearchIndexName( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }


}
