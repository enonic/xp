package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.DeleteNodeById;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
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

    public DeleteNodeResult execute()
        throws Exception
    {
        try
        {
            nodeJcrDao.deleteNodeById( command.getId() );
            session.save();
            indexService.deleteEntity( command.getId() );
            return DeleteNodeResult.SUCCESS;

        }
        catch ( NoNodeAtPathFound e )
        {
            return DeleteNodeResult.NOT_FOUND;
        }
    }

}
