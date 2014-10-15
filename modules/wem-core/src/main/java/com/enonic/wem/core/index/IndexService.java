package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.Set;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.Node;

public interface IndexService
{
    public void createIndex( final String indexName, final String settings );

    public void deleteIndex( final Collection<String> indexNames );

    public boolean indicesExists( final String... indices );

    public void store( final Node node, final IndexContext context );

    public void update( final EntityId entityId, final UpdateScript updateScript, final IndexContext context );

    public void delete( final EntityId entityId, final IndexContext context );

    public Set<String> getAllRepositoryIndices( final RepositoryId repositoryId );

    public void applyMapping( final String indexName, final String indexType, final String mapping );
}

