package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.GetNodesByParentParams;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetNodesByParentCommand
{
    private GetNodesByParentParams params;

    private Session session;

    public Nodes execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Nodes doExecute()
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        return nodeJcrDao.getNodesByParentPath( params.getParent() );
    }

    public GetNodesByParentCommand params( final GetNodesByParentParams params )
    {
        this.params = params;
        return this;
    }

    public GetNodesByParentCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
