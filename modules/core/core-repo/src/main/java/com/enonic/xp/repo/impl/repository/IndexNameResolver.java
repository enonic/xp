package com.enonic.xp.repo.impl.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class IndexNameResolver
{
    private static final String SEARCH_INDEX_PREFIX = "search";

    private static final String STORAGE_INDEX_PREFIX = "storage";

    private static final String COMMIT_INDEX_PREFIX = "commit";

    private static final String DIVIDER = "-";

    public static String resolveIndexName( final RepositoryId repositoryId, final IndexType indexType )
    {
        return resolveIndexName( repositoryId, indexType, ContextAccessor.current().getBranch() );
    }

    public static String resolveIndexName( final RepositoryId repositoryId, final IndexType indexType, final Branch branch )
    {
        switch ( indexType )
        {
            case STORAGE:
                return resolveStorageIndexName( repositoryId );
            case COMMIT:
                return resolveCommitIndexName( repositoryId );
            case SEARCH:
                return branch != null ? resolveSearchIndexName( repositoryId, branch ) : null;
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

    public static Set<String> resolveSearchIndexNames( final Repository repository )
    {
        return repository.getBranches().stream().
            map( branch -> IndexNameResolver.resolveSearchIndexName( repository.getId(), branch ) ).
            collect( Collectors.toSet() );
    }

    public static String resolveSearchIndexPrefix( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString() + DIVIDER + "*";
    }

    public static Set<String> resolveIndexNames( final Repository repository )
    {
        final Set<String> indexNames = new HashSet<>();

        indexNames.add( IndexNameResolver.resolveCommitIndexName( repository.getId() ) );
        indexNames.add( IndexNameResolver.resolveStorageIndexName( repository.getId() ) );
        indexNames.addAll( IndexNameResolver.resolveSearchIndexNames( repository ) );

        return Collections.unmodifiableSet( indexNames );
    }

    public static Set<String> resolveIndexNames( final Set<Repository> repositories )
    {
        return repositories.stream().
            flatMap( repository -> IndexNameResolver.resolveIndexNames( repository ).stream() ).
            collect( Collectors.toUnmodifiableSet() );
    }

}
