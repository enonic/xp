package com.enonic.wem.core.entity;


import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.entity.UpdateNodeResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.EntityDao;
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

        final Node persisted = nodeJcrDao.getNodeById( command.getNode() );

        final Node.EditBuilder editBuilder = command.getEditor().edit( persisted );
        if ( !editBuilder.isChanges() )
        {
            command.setResult( new UpdateNodeResult( persisted ) );
            return;
        }

        final Node edited = editBuilder.build();
        persisted.checkIllegalEdit( edited );

        final UpdateNodeArgs updateNodeArgs = newUpdateItemArgs().
            nodeToUpdate( command.getNode() ).
            name( edited.name() ).
            icon( edited.icon() ).
            rootDataSet( edited.data() ).
            build();

        final Node persistedNode = nodeJcrDao.updateNode( updateNodeArgs );
        session.save();

        indexService.indexNode( persistedNode );

        final EntityDao.UpdateEntityArgs updateEntityArgs = new EntityDao.UpdateEntityArgs.Builder().
            entityToUpdate( persisted.id() ).
            data( edited.data() ).
            build();
        //Commented out because of problems with mixin update
        //entityDao.update( updateEntityArgs );

        command.setResult( new UpdateNodeResult( persistedNode ) );
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
