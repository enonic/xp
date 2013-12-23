package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.DeleteNodeById;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByIdService
    extends NodeService
{
    private final IndexService indexService;

    private final DeleteNodeById command;

    public DeleteNodeByIdService( final Session session, final IndexService indexService, final DeleteNodeById command )
    {
        super( session );
        this.indexService = indexService;
        this.command = command;
    }

    public void execute()
        throws Exception
    {
        nodeJcrDao.deleteNodeById( command.getId() );
        session.save();

        indexService.deleteEntity( command.getId() );
    }
}
