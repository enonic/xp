package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.GetNodesByParentParams;
import com.enonic.wem.api.entity.Nodes;

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
        return new GetNodesByParentService( session, params.getParent() ).execute();
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
