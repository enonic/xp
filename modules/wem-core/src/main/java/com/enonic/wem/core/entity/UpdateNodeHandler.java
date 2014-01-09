package com.enonic.wem.core.entity;


import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.entity.UpdateNodeResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.entity.dao.UpdateNodeArgs;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.core.entity.dao.UpdateNodeArgs.newUpdateItemArgs;

public class UpdateNodeHandler
    extends CommandHandler<UpdateNode>
{

    private IndexService indexService;

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    public UpdateNodeHandler()
    {
    }

    public UpdateNodeHandler( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.context = builder.context;
        this.command = builder.command;
    }

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();

        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );

        final Node beforeChange = nodeJcrDao.getNodeById( command.getId() );

        final Node.EditBuilder editBuilder = command.getEditor().edit( beforeChange );
        if ( !editBuilder.isChanges() )
        {
            command.setResult( new UpdateNodeResult( beforeChange ) );
            return;
        }

        final Node edited = editBuilder.build();
        beforeChange.checkIllegalEdit( edited );

        final UpdateNodeArgs updateNodeArgs = newUpdateItemArgs().
            nodeToUpdate( command.getId() ).
            name( edited.name() ).
            rootDataSet( edited.data() ).
            attachments( edited.attachments() ).

            build();

        final Node updatedNode = nodeJcrDao.updateNode( updateNodeArgs );
        session.save();

        indexService.indexNode( updatedNode );

        command.setResult( new UpdateNodeResult( updatedNode ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IndexService indexService;

        private CommandContext context;

        private UpdateNode command;

        public Builder indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        public Builder context( final CommandContext context )
        {
            this.context = context;
            return this;
        }

        public Builder command( final UpdateNode command )
        {
            this.command = command;
            return this;
        }

        public UpdateNodeHandler build()
        {
            return new UpdateNodeHandler( this );
        }
    }

}
