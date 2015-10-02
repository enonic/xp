package com.enonic.xp.repo.impl.index;

import org.elasticsearch.common.unit.TimeValue;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.elasticsearch.ClusterHealthStatus;

public interface IndexServiceInternal
{
    void createIndex( final String indexName, final IndexSettings settings );

    void deleteIndices( final String... indexNames );

    boolean indicesExists( final String... indices );

    void store( final Node node, final NodeVersionId nodeVersionId, final InternalContext context );

    void delete( final NodeId nodeId, final InternalContext context );

    void applyMapping( final String indexName, final IndexType indexType, final String mapping );

    ClusterHealthStatus getClusterHealth( final TimeValue timeout, final String... indexNames );

    void refresh( final String... indexNames );

}

