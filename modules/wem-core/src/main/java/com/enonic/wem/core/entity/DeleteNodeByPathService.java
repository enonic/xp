package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByPathService
    extends NodeService
{
    private IndexService indexService;

    private DeleteNodeByPath command;

    public DeleteNodeByPathService( final Session session, final IndexService indexService, final DeleteNodeByPath command )
    {
        super( session );
        this.indexService = indexService;
        this.command = command;
    }

    public DeleteNodeResult execute()
        throws Exception
    {
        final Node nodeByPath = nodeJcrDao.getNodeByPath( command.getPath() );

        if ( nodeByPath == null )
        {
            return DeleteNodeResult.NOT_FOUND;
        }

        try
        {
            nodeJcrDao.deleteNodeByPath( command.getPath() );
            session.save();
            indexService.deleteEntity( nodeByPath.id() );

            return DeleteNodeResult.SUCCESS;

        }
        catch ( NoNodeAtPathFound e )
        {
            return DeleteNodeResult.NOT_FOUND;
        }
    }

}
