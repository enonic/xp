package com.enonic.wem.core.index;

import java.util.Set;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Workspace;

public interface IndexService
{
    public void createIndex( final Index index );

    public void deleteIndex( final Index... indexes );

    public void deleteIndex( final String... indexNames );

    public void index( final Node node, final Workspace workspace );

    public void delete( final EntityId entityId, final Workspace workspace );

    public long countDocuments( final Index index );

    public Set<String> getAllIndicesNames();

}

