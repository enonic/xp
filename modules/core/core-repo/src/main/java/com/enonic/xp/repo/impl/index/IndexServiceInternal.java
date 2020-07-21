package com.enonic.xp.repo.impl.index;

import java.util.Map;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

public interface IndexServiceInternal
{
    void createIndex( final CreateIndexRequest request );

    void updateIndex( final String indexName, final UpdateIndexSettings settings );

    void deleteIndices( final String... indexNames );

    boolean indicesExists( final String... indices );

    void closeIndices( final String... indices );

    void openIndices( final String... indices );

    void applyMapping( final ApplyMappingRequest request );

    boolean waitForYellowStatus( String... indexNames );

    IndexSettings getIndexSettings( final RepositoryId repositoryId, final IndexType indexType );

    Map<String, Object> getIndexMapping( final RepositoryId repositoryId, final Branch branch, final IndexType indexType );

    void refresh( final String... indexNames );

    boolean isMaster();

    void copy( final NodeId nodeId, final RepositoryId repositoryId, final Branch source, final Branch target );
}

