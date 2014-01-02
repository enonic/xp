package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.UnableToDeleteMixinException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.DeleteNodeByPathService;
import com.enonic.wem.core.index.IndexService;


public final class DeleteMixinHandler
    extends CommandHandler<DeleteMixin>
{
    private final static MixinNodeTranslator MIXIN_NODE_TRANSLATOR = new MixinNodeTranslator();

    private IndexService indexService;

    @Override
    public void handle()
        throws Exception
    {
        final DeleteNodeByPath deleteNodeByPathCommand =
            Commands.node().delete().byPath( new NodePath( "/mixins/" + command.getName().toString() ) );

        try
        {
            checkNotBeingUsed();

            final Node deletedNode =
                new DeleteNodeByPathService( context.getJcrSession(), indexService, deleteNodeByPathCommand ).execute();

            final Mixin mixin = MIXIN_NODE_TRANSLATOR.fromNode( deletedNode );
            command.setResult( new DeleteMixinResult( mixin ) );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new MixinNotFoundException( command.getName() );
        }
    }

    private void checkNotBeingUsed()
    {
        // TODO: Replace with search
        final Mixin mixinToDelete = context.getClient().execute( Commands.mixin().get().byName( command.getName() ) );
        final ContentTypes allContentTypes = context.getClient().execute( Commands.contentType().get().all() );
        final ContentTypes usingContentTypes = new MixinUsageResolver( mixinToDelete ).resolveUsingContentTypes( allContentTypes );
        if ( usingContentTypes.isNotEmpty() )
        {
            throw new UnableToDeleteMixinException( command.getName(), "Mixin is being used" );
        }
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
