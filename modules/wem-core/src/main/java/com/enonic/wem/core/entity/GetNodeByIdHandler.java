package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetNodeByIdHandler
    extends CommandHandler<GetNodeById>
{
    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao itemDao = new NodeJcrDao( session );

        final Node persistedNode = itemDao.getNodeById( command.getId() );
        command.setResult( persistedNode );
    }
}
