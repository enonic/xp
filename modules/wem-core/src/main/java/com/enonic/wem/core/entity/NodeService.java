package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.core.entity.dao.NodeJcrDao;

public abstract class NodeService
{
    NodeJcrDao nodeJcrDao;

    public NodeService( final Session session )
    {
        this.nodeJcrDao = new NodeJcrDao( session );
    }


}
