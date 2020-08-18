package com.enonic.xp.repo.impl.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public class IndexNameResolver
{
    private static final String SEARCH_INDEX_PREFIX = "search";

    private static final String STORAGE_INDEX_PREFIX = "storage";

    private static final String COMMIT_INDEX_PREFIX = "commit";

    private static final String DIVIDER = "-";

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

    public static String resolveSearchIndexName( final RepositoryId repositoryId, final Branch branch )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + branch.getValue().toLowerCase();
    }

    public static Set<String> resolveSearchIndexNames( final RepositoryId repositoryId, final Branches branches )
    {
        return branches.stream().
            map( branch -> IndexNameResolver.resolveSearchIndexName( repositoryId, branch ) ).
            collect( Collectors.toSet() );
    }

    public static String resolveSearchIndexPrefix( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + "*";
    }

    public static Set<String> resolveIndexNames( final RepositoryId repositoryId )
    {
        return Stream.of( IndexNameResolver.resolveStorageIndexName( repositoryId )/*, // TODO should be fixed
                          IndexNameResolver.resolveSearchIndexName( repositoryId )*/ ).
            collect( Collectors.toUnmodifiableSet() );
    }

    public static Set<String> resolveIndexNames( final RepositoryId repositoryId, final Branches branches )
    {
        final Set<String> indexNames = new HashSet<>();

        indexNames.add( IndexNameResolver.resolveStorageIndexName( repositoryId ) );
        indexNames.addAll( IndexNameResolver.resolveSearchIndexNames( repositoryId, branches ) );

        return Collections.unmodifiableSet( indexNames );
    }

    public static Set<String> resolveIndexNames( final RepositoryIds repositoryIds )
    {
        return repositoryIds.stream().
            flatMap( repositoryId -> IndexNameResolver.resolveIndexNames( repositoryId ).stream() ).
            collect( Collectors.toUnmodifiableSet() );
    }

}
