package com.enonic.xp.repo.impl.repository;

import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.RepositoryId;

public class IndexNameResolver
{
    private final static String SEARCH_INDEX_PREFIX = "search";

    private final static String VERSION_INDEX_PREFIX = "version";

    private final static String BRANCH_INDEX_PREFIX = "branch";

    private final static String COMMIT_INDEX_PREFIX = "commit";

    private final static String DIVIDER = "-";

    public static String resolveStorageIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        if ( repositoryId == null || indexType == null )
        {
            return null;
        }

        switch ( indexType )
        {
            case VERSION:
                return resolveVersionIndexName( repositoryId );
            case BRANCH:
                return resolveBranchIndexName( repositoryId );
            case COMMIT:
                return resolveCommitIndexName( repositoryId );
            default:
                return null;
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
