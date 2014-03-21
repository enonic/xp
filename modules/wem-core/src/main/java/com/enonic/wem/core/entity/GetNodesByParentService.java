package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;

public class GetNodesByParentService
    extends AbstractNodeService
{
    private final NodePath nodePath;

    public GetNodesByParentService( final Session session, final NodePath nodePath )
    {
        super( session );
        this.nodePath = nodePath;
    }

    public Nodes execute()
    {
        return nodeJcrDao.getNodesByParentPath( nodePath );
    }

}
