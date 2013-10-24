package com.enonic.wem.core.item;


import javax.jcr.Session;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.UpdateEntityResult;
import com.enonic.wem.api.entity.UpdateNode;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.NodeJcrDao;
import com.enonic.wem.core.item.dao.UpdateNodeArgs;

import static com.enonic.wem.core.item.dao.UpdateNodeArgs.newUpdateItemArgs;

public class UpdateNodeHandler
    extends CommandHandler<UpdateNode>
{
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
            build();

        final Node persistedNode = itemDao.updateNode( updateNodeArgs );
        session.save();
        // TODO: update index for item or in dao?

        command.setResult( new UpdateEntityResult( persistedNode ) );
    }
}
