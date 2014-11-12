package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.Set;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.core.elasticsearch.IndexStatus;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeVersionId;

public interface IndexService
{
    public void createIndex( final String indexName, final String settings );

    public void deleteIndices( final Collection<String> indexNames );

    public boolean indicesExists( final String... indices );

    public void store( final Node node, final NodeVersionId nodeVersionId, final IndexContext context );

    public void delete( final NodeId nodeId, final IndexContext context );

    public Set<String> getAllRepositoryIndices( final RepositoryId repositoryId );

    public void applyMapping( final String indexName, final String indexType, final String mapping );

    public IndexStatus getIndexStatus( final boolean waitForYellowStatus, final String... indexNames );

    public void refresh( final String... indexNames );
}

