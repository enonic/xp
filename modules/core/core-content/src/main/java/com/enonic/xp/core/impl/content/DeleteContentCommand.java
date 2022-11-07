package com.enonic.xp.core.impl.content;


import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;


final class DeleteContentCommand
    extends AbstractContentCommand
    implements DeleteNodeListener
{
    private final DeleteContentParams params;

    private DeleteContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    DeleteContentsResult execute()
    {
        try
        {
            return doExecute();
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private DeleteContentsResult doExecute()
    {
        this.nodeService.refresh( RefreshMode.ALL );

        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.params.getContentPath() );
        final Node nodeToDelete = this.nodeService.getByPath( nodePath );

        if ( nodeToDelete == null )
        {
            throw new ContentNotFoundException( this.params.getContentPath(), ContextAccessor.current().getBranch() );
        }

        final DeleteContentsResult deletedContents = doDeleteContent( nodeToDelete.id() );

        this.nodeService.refresh( RefreshMode.ALL );

        return deletedContents;
    }

    private DeleteContentsResult doDeleteContent( NodeId nodeToDelete )
    {
        final DeleteContentsResult.Builder result = DeleteContentsResult.create();

        final ContentIds unpublishedContents = unpublish( nodeToDelete );
        result.addUnpublished( unpublishedContents );

        final NodeIds deletedNodes = this.nodeService.deleteById( nodeToDelete, this );
        final ContentIds deletedContents = ContentNodeHelper.toContentIds( deletedNodes );

        result.addDeleted( deletedContents );

        return result.build();
    }

    @Override
    public void nodesDeleted( final int count )
    {
        if ( params.getDeleteContentListener() != null )
        {
            params.getDeleteContentListener().contentDeleted( count );
        }
    }

    @Override
    public void totalToDelete( final int count )
    {
        if ( params.getDeleteContentListener() != null )
        {
            params.getDeleteContentListener().setTotal( count );
        }
    }

    private ContentIds unpublish( final NodeId nodeId )
    {
        final FindNodesByParentResult childrenInDraft =
            nodeService.findByParent( FindNodesByParentParams.create().size( -1 ).recursive( true ).parentId( nodeId ).build() );

        return UnpublishContentCommand.create()
            .nodeService( nodeService )
            .contentTypeService( contentTypeService )
            .translator( translator )
            .eventPublisher( eventPublisher )
            .params( UnpublishContentParams.create()
                         .contentIds( ContentIds.create()
                                          .addAll( ContentNodeHelper.toContentIds( childrenInDraft.getNodeIds() ) )
                                          .add( ContentId.from( nodeId ) )
                                          .build() )
                         .build() )
            .build()
            .execute()
            .getUnpublishedContents();
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

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public DeleteContentCommand build()
        {
            validate();
            return new DeleteContentCommand( this );
        }
    }

}
