package com.enonic.xp.repo.impl.repository;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public class IndexNameResolver
{
    private final static String SEARCH_INDEX_PREFIX = "search";

    private final static String STORAGE_INDEX_PREFIX = "storage";

    private final static String DIVIDER = "-";

    public static String resolveStorageIndexName( final RepositoryId repositoryId )
    {
        return STORAGE_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    public static String resolveSearchIndexName( final RepositoryId repositoryId )
    {
        return SEARCH_INDEX_PREFIX + DIVIDER + repositoryId.toString();
    }

    public static Set<String> resolveIndexNames( final RepositoryId repositoryId )
    {
        return Stream.of( IndexNameResolver.resolveStorageIndexName( repositoryId ),
                          IndexNameResolver.resolveSearchIndexName( repositoryId ) ).
            collect( Collectors.toUnmodifiableSet() );
    }

    public static Set<String> resolveIndexNames( final RepositoryIds repositoryIds )
    {
        return repositoryIds.stream().
            flatMap( repositoryId -> IndexNameResolver.resolveIndexNames( repositoryId ).stream() ).
            collect( Collectors.toUnmodifiableSet() );
    }
}
