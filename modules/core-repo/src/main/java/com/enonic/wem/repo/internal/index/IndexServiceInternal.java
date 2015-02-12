package com.enonic.wem.repo.internal.index;

import java.util.Set;

import org.elasticsearch.common.unit.TimeValue;

import com.enonic.wem.api.index.IndexType;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.repo.internal.elasticsearch.ClusterHealthStatus;

public interface IndexServiceInternal
{
    public void createIndex( final String indexName, final String settings );

    public void deleteIndices( final String... indexNames );

    public boolean indicesExists( final String... indices );

    public void store( final Node node, final NodeVersionId nodeVersionId, final IndexContext context );

    public void delete( final NodeId nodeId, final IndexContext context );

    public Set<String> getAllRepositoryIndices( final RepositoryId repositoryId );

    public void applyMapping( final String indexName, final IndexType indexType, final String mapping );

    public ClusterHealthStatus getClusterHealth( final TimeValue timeout, final String... indexNames );

    public void refresh( final String... indexNames );

}

