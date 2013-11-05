package com.enonic.wem.core.entity;


import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.entity.UpdateNodeResult;
import com.enonic.wem.api.entity.Node;
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

    private EntityDao entityDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();

        final NodeJcrDao itemDao = new NodeJcrDao( session );

        final Node persisted = itemDao.getNodeById( command.getNode() );

        Node edited = command.getEditor().edit( persisted );
        if ( edited == null )
        {
            // TODO: set status NO CHANGE?
            return;
        }

        persisted.checkIllegalEdit( edited );

        final UpdateNodeArgs updateNodeArgs = newUpdateItemArgs().
            itemToUpdate( command.getNode() ).
            name( edited.name() ).
            icon( edited.icon() ).
            rootDataSet( edited.data() ).
            build();

        final Node persistedNode = itemDao.updateNode( updateNodeArgs );
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

    @Inject
    public void setEntityDao( final EntityDao entityDao )
    {
        this.entityDao = entityDao;
    }
}
