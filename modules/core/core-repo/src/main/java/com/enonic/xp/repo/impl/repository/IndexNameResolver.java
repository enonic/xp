package com.enonic.xp.repo.impl.repository;

import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.RepositoryId;

public class IndexNameResolver
{
    private final static String SEARCH_INDEX_PREFIX = "search";

    private final static String STORAGE_INDEX_PREFIX = "storage";

    private final static String COMMIT_INDEX_PREFIX = "commit";

    private final static String DIVIDER = "-";

    public static String resolveIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        switch ( indexType )
        {
            case STORAGE:
                return resolveStorageIndexName( repositoryId );
            case COMMIT:
                return resolveCommitIndexName( repositoryId );
            case SEARCH:
                return resolveSearchIndexName( repositoryId, ContextAccessor.current().getBranch() );
        }

        return null;
    }

    public static String resolveStorageIndexName( final RepositoryId repositoryId )
    {
        return STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    public static String resolveCommitIndexName( final RepositoryId repositoryId )
    {
        return COMMIT_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    public static Set<String> resolveSearchIndexNames( final RepositoryId repositoryId, final Branches branches )
    {
        return branches.stream().
            map( branch -> IndexNameResolver.resolveSearchIndexName( repositoryId, branch ) ).
            collect( Collectors.toSet() );
    }

    public static String resolveSearchIndexName( final RepositoryId repositoryId, final Branch branch )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + branch.getValue().toLowerCase();
    }

    static String resolveSearchIndexPrefix( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + "*";
    }
}
