package com.enonic.wem.core.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.DeleteContentParams;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.NoNodeAtPathFoundException;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;


final class DeleteContentCommand
    extends AbstractContentCommand
{
    private final DeleteContentParams params;

    private DeleteContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    DeleteContentResult execute()
    {
        params.validate();

        return doExecute();
    }

    private DeleteContentResult doExecute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.params.getContentPath() );

        try
        {
            final Node deletedNode = nodeService.deleteByPath( nodePath );

            final Content deletedContent = translator.fromNode( deletedNode );

            return new DeleteContentResult( deletedContent );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new ContentNotFoundException( this.params.getContentPath(), ContextAccessor.current().getWorkspace() );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private DeleteContentParams params;

        public Builder params( final DeleteContentParams params )
        {
            this.params = params;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public DeleteContentCommand build()
        {
            validate();
            return new DeleteContentCommand( this );
        }
    }

}
