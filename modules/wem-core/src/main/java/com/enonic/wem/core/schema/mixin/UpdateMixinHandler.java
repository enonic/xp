package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.UpdateNode;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.NodeDao;
import com.enonic.wem.core.item.dao.NodeJcrDao;


public final class UpdateMixinHandler
    extends CommandHandler<UpdateMixin>
{
    private final static MixinItemTranslator MIXIN_TO_ITEM_TRANSLATOR = new MixinItemTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final NodeDao nodeDao = new NodeJcrDao( context.getJcrSession() );

        final Mixin mixin = getMixin( command.getQualifiedName(), nodeDao );
        if ( mixin == null )
        {
            command.setResult( UpdateMixinResult.NOT_FOUND );
        }
        else
        {
            final Mixin modifiedMixin = command.getEditor().edit( mixin );
            if ( modifiedMixin != null )
            {
                final NodeEditor nodeEditor = MIXIN_TO_ITEM_TRANSLATOR.toItemEditor( modifiedMixin );
                UpdateNode updateNode = MIXIN_TO_ITEM_TRANSLATOR.toUpdateItemCommand( mixin.getId(), nodeEditor );
                context.getClient().execute( updateNode );
                command.setResult( UpdateMixinResult.SUCCESS );
            }
            command.setResult( UpdateMixinResult.SUCCESS );
        }
    }

    private Mixin getMixin( final QualifiedMixinName qualifiedMixinName, NodeDao nodeDao )
    {
        // TODO: Use API when ready
        final Node mixinNode = nodeDao.getNodeByPath( new NodePath( "/mixins/" + qualifiedMixinName.toString() ) );
        return MIXIN_TO_ITEM_TRANSLATOR.fromItem( mixinNode );
    }
}
