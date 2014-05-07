package com.enonic.wem.core.index;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;

public interface IndexService
{
    public void createIndex( final Index index );

    public void deleteIndex( final Index... indexes );

    public void indexNode( final Node node );

    public void deleteEntity( final EntityId entityId );

    public void setDoReindexOnEmptyIndex( final boolean doReindexOnEmptyIndex );

}
