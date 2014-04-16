package com.enonic.wem.core.entity;


import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.NodeElasticsearchDao;

final class GetNodesByIdsCommand
{
    private EntityIds entityIds;

    private NodeElasticsearchDao nodeElasticsearchDao;

    Nodes execute()
    {
        return nodeElasticsearchDao.getByIds( this.entityIds );
    }

    GetNodesByIdsCommand entityIds( final EntityIds entityIds )
    {
        this.entityIds = entityIds;
        return this;
    }

    GetNodesByIdsCommand nodeElasticsearchDao( final NodeElasticsearchDao nodeElasticsearchDao )
    {
        this.nodeElasticsearchDao = nodeElasticsearchDao;
        return this;
    }
}
