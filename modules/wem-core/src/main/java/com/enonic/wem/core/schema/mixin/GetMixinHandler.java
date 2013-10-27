package com.enonic.wem.core.schema.mixin;

import com.google.common.annotations.VisibleForTesting;

import com.enonic.wem.api.command.schema.mixin.GetMixin;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeJcrDao;


public final class GetMixinHandler
    extends CommandHandler<GetMixin>
{
    private final static MixinItemTranslator MIXIN_TO_ITEM_TRANSLATOR = new MixinItemTranslator();

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

        try
        {
            final Mixin mixin = getMixin( command.getQualifiedName(), nodeDao );
            command.setResult( mixin );
        }
        catch ( NoNodeAtPathFound e )
        {
            command.setResult( null );
        }
    }

    private Mixin getMixin( final QualifiedMixinName qualifiedName, final NodeDao nodeDao )
    {
        // TODO: Use Node API when ready to fetch node
        final Node node = nodeDao.getNodeByPath( new NodePath( "/mixins/" + qualifiedName.toString() ) );
        return MIXIN_TO_ITEM_TRANSLATOR.fromItem( node );
    }
}
