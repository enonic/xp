package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.GetNodeByIdParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetNodeByIdCommand
{
    private GetNodeByIdParams params;

    private Session session;

    public Node execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Node doExecute()
    {
        NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        return nodeJcrDao.getNodeById( this.params.getId() );
    }

    public GetNodeByIdCommand params( final GetNodeByIdParams params )
    {
        this.params = params;
        return this;
    }

    public GetNodeByIdCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
