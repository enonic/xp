package com.enonic.xp.core.impl.content;


import java.util.Objects;

import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;


final class DeleteContentCommand
    extends AbstractContentCommand
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

        final ContentPath contentPath = this.params.getContentPath();
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );
        final Node nodeToDelete = this.nodeService.getByPath( nodePath );

        if ( nodeToDelete == null )
        {
            throw  ContentNotFoundException.create()
            .contentPath( contentPath )
            .repositoryId( ContextAccessor.current().getRepositoryId() )
            .branch( ContextAccessor.current().getBranch() )
            .contentRoot( ContentNodeHelper.getContentRoot() )
            .build();
        }

        return doDeleteContent( ContentId.from( nodeToDelete.id() ) );
    }

    private DeleteContentsResult doDeleteContent( ContentId nodeToDelete )
    {
        final DeleteContentsResult.Builder result = DeleteContentsResult.create();

        final NodeId nodeId = NodeId.from( nodeToDelete );

        final NodeIds descendants =
            nodeService.findByParent( FindNodesByParentParams.create().recursive( true ).parentId( nodeId ).build() ).getNodeIds();

        final ContentIds unpublishedContents = unpublish( nodeToDelete, ContentNodeHelper.toContentIds( descendants ) );
        result.addUnpublished( unpublishedContents );

        final DeleteNodeParams.Builder builder = DeleteNodeParams.create()
            .nodeId( nodeId )
            .refresh( RefreshMode.SEARCH );

        if ( params.getDeleteContentListener() != null )
        {
            builder.deleteNodeListener( params.getDeleteContentListener()::contentDeleted );
        }

        final DeleteNodeResult deletedNodes = this.nodeService.delete( builder.build() );

        result.addDeleted( ContentNodeHelper.toContentIds( deletedNodes.getNodeIds() ) );

        return result.build();
    }

    private ContentIds unpublish( final ContentId contentId, final ContentIds descendants )
    {
        return UnpublishContentCommand.create()
            .nodeService( nodeService )
            .contentTypeService( contentTypeService )
            .translator( translator )
            .eventPublisher( eventPublisher )
            .params( UnpublishContentParams.create()
                         .contentIds( ContentIds.create()
                                          .addAll( descendants )
                                          .add( contentId )
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
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public DeleteContentCommand build()
        {
            validate();
            return new DeleteContentCommand( this );
        }
    }
}
