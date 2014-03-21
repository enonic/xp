package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.GetNodesByPathsParams;
import com.enonic.wem.api.entity.Nodes;

public class GetNodesByPathsCommand
{
    private GetNodesByPathsParams params;

    private Session session;

    public Nodes execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Nodes doExecute()
    {
        return new GetNodesByPathsService( session, params.getPaths() ).execute();
    }

    public GetNodesByPathsCommand params( final GetNodesByPathsParams params )
    {
        this.params = params;
        return this;
    }

    public GetNodesByPathsCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
