package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.core.entity.dao.NodeJcrDao;

public abstract class NodeService
{
    NodeJcrDao nodeJcrDao;

    Session session;

    public NodeService( final Session session )
    {
        this.session = session;
        this.nodeJcrDao = new NodeJcrDao( session );
    }


}
