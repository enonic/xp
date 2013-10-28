package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class DeleteNodeByPathHandler
    extends CommandHandler<DeleteNodeByPath>
{
    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao itemDao = new NodeJcrDao( session );

        try
        {
            itemDao.deleteNodeByPath( command.getPath() );
            session.save();
            command.setResult( DeleteNodeResult.SUCCESS );

            // TODO: delete from index
        }
        catch ( NoNodeAtPathFound e )
        {
            command.setResult( DeleteNodeResult.NOT_FOUND );
        }
    }
}
