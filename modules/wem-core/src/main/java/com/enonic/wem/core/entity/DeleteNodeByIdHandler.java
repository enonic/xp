package com.enonic.wem.core.entity;

import javax.inject.Inject;

import com.enonic.wem.api.command.entity.DeleteNodeById;
import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByIdHandler
    extends CommandHandler<DeleteNodeById>
{
    private IndexService indexService;

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Override
    public void handle()
        throws Exception
    {
        final Node nodeToDelete = new GetNodeByIdService( this.context.getJcrSession(), new GetNodeById( this.command.getId() ) ).execute();

        new DeleteNodeByIdService( this.context.getJcrSession(), this.indexService, this.command ).execute();

        command.setResult( nodeToDelete );

    }

}
