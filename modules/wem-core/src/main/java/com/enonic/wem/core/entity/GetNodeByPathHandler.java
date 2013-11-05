package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetNodeByPathHandler
    extends CommandHandler<GetNodeByPath>
{
    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao itemDao = new NodeJcrDao( session );

        final Node persistedNode = itemDao.getNodeByPath( command.getPath() );
        command.setResult( persistedNode );
    }
}
