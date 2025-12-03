package com.enonic.xp.repo.impl.index;

import java.util.Map;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.RepositoryId;

public interface IndexServiceInternal
{
    void createIndex( CreateIndexRequest request );

    void updateIndex( String indexName, UpdateIndexSettings settings );

    void deleteIndices( String... indexNames );

    boolean indicesExists( String... indices );

    void closeIndices( String... indices );

    void openIndices( String... indices );

    boolean waitForYellowStatus( String... indexNames );

    Map<String, String> getIndexSettings( RepositoryId repositoryId, IndexType indexType );

    void putIndexMapping( RepositoryId repositoryId, IndexType indexType, Map<String,Object> mapping );

    void refresh( String... indexNames );

    boolean isMaster();
}

