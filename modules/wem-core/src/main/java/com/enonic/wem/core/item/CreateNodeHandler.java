package com.enonic.wem.core.item;


import javax.jcr.Session;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.CreateNode;
import com.enonic.wem.api.entity.CreateNodeResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.CreateNodeArguments;
import com.enonic.wem.core.item.dao.NodeJcrDao;

public class CreateNodeHandler
    extends CommandHandler<CreateNode>
{
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
        // TODO: index item or in dao?
    }
}
