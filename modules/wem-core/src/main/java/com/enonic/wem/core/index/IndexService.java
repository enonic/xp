package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.Set;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;

public interface IndexService
{
    public void createIndex( final String indexName, final String settings );

    public void deleteIndex( final Collection<String> indexNames );

    public void index( final Node node, final Workspace workspace );

    public void delete( final EntityId entityId, final Workspace workspace );

    public Set<String> getAllRepositoryIndices( final Repository repository );

    public void applyMapping( final String indexName, final String indexType, final String mapping );
}

