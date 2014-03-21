package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;

public class GetNodeByPathService
    extends AbstractNodeService
{
    private final NodePath nodePath;

    public GetNodeByPathService( final Session session, final NodePath nodePath )
    {
        super( session );
        this.nodePath = nodePath;
    }

    public Node execute()
    {
        return nodeJcrDao.getNodeByPath( nodePath );
    }

}
