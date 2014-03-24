package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.GetNodeByPathParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetNodeByPathCommand
{
    private GetNodeByPathParams params;

    private Session session;

    public Node execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Node doExecute()
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        return nodeJcrDao.getNodeByPath( params.getPath() );
    }

    public GetNodeByPathCommand params( final GetNodeByPathParams params )
    {
        this.params = params;
        return this;
    }

    public GetNodeByPathCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
