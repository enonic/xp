package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.GetNodesByIdsParams;
import com.enonic.wem.api.entity.Nodes;

public class GetNodesByIdsCommand
{
    private GetNodesByIdsParams params;

    private Session session;

    public Nodes execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Nodes doExecute()
    {
        return new GetNodesByIdsService( session, params.getIds() ).execute();
    }

    public GetNodesByIdsCommand params( final GetNodesByIdsParams params )
    {
        this.params = params;
        return this;
    }

    public GetNodesByIdsCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
