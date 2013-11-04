package com.enonic.wem.core.entity;


import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.CreateNodeArguments;
import com.enonic.wem.core.entity.dao.EntityDao;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;

public class CreateNodeHandler
    extends CommandHandler<CreateNode>
{
    private IndexService indexService;


    private EntityDao entityDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao itemDao = new NodeJcrDao( session );

        final CreateNodeArguments createNodeArguments = CreateNodeArguments.newCreateNodeArgs().
            creator( UserKey.superUser() ).
            parent( command.getParent() ).
            name( command.getName() ).
            icon( command.getIcon() ).
            rootDataSet( command.getData() ).
            build();

        final Node persistedNode = itemDao.createNode( createNodeArguments );
        session.save();

        command.setResult( new CreateNodeResult( persistedNode ) );

        final EntityDao.CreateEntityArgs createEntityArgs = new EntityDao.CreateEntityArgs.Builder().
            data( command.getData() ).
            build();
        entityDao.create( createEntityArgs );

        indexService.indexNode( persistedNode );
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
        // TODO: index item or in dao?
    }

    @Inject
    public void setEntityDao( final EntityDao entityDao )
    {
        this.entityDao = entityDao;
    }
}
