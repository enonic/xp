package com.enonic.wem.core.schema.mixin;

import com.google.common.annotations.VisibleForTesting;

import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeJcrDao;


public final class DeleteMixinHandler
    extends CommandHandler<DeleteMixin>
{
    private NodeDao nodeDao;

    @VisibleForTesting
    public void setNodeJcrDao( NodeJcrDao nodeDao )
    {
        this.nodeDao = nodeDao;
    }

    @Override
    public void handle()
        throws Exception
    {
        if ( this.nodeDao == null )
        {
            nodeDao = new NodeJcrDao( context.getJcrSession() );
        }

        final QualifiedMixinName qualifiedMixinName = command.getName();
        try
        {

            nodeDao.deleteNodeByPath( new NodePath( "/mixins/" + qualifiedMixinName.toString() ) );
            context.getJcrSession().save();
            command.setResult( DeleteMixinResult.SUCCESS );
        }
        catch ( NoNodeAtPathFound e )
        {
            command.setResult( DeleteMixinResult.NOT_FOUND );
        }
    }
}
