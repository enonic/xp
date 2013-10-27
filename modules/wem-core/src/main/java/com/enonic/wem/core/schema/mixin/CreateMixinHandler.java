package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.CommandHandler;


public final class CreateMixinHandler
    extends CommandHandler<CreateMixin>
{
    private final static MixinItemTranslator MIXIN_TO_ITEM_TRANSLATOR = new MixinItemTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final CreateNode createNodeCommand = MIXIN_TO_ITEM_TRANSLATOR.toCreateItemCommand( command );
        final CreateNodeResult createNodeResult = context.getClient().execute( createNodeCommand );

        final Mixin persistedMixin = MIXIN_TO_ITEM_TRANSLATOR.fromItem( createNodeResult.getPersistedNode() );
        command.setResult( persistedMixin.getQualifiedName() );
    }
}
