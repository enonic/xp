package com.enonic.xp.core.impl.content;


import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentListener;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

import static java.util.Objects.requireNonNull;


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
            throw ContentNodeHelper.toContentAccessException( e );
        }
    }

    private DeleteContentsResult doExecute()
    {

        final ContentPath contentPath = this.params.getContentPath();
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( contentPath );
        final Node nodeToDelete = this.nodeService.getByPath( nodePath );

        if ( nodeToDelete == null )
        {
            throw ContentNotFoundException.create()
                .contentPath( contentPath )
                .repositoryId( ContextAccessor.current().getRepositoryId() )
                .branch( ContextAccessor.current().getBranch() )
                .contentRoot( ContentNodeHelper.getContentRoot() )
                .build();
        }

        verifyNotProtectedRoot( nodeToDelete.path() );

        return doDeleteContent( nodeToDelete );
    }

    private DeleteContentsResult doDeleteContent( final Node nodeToDelete )
    {
        final DeleteContentsResult.Builder result = DeleteContentsResult.create();

        final NodeId nodeId = nodeToDelete.id();
        final ContentId contentId = ContentId.from( nodeId );

        // enumerated, not searched: everything below has to be unpublished, including what a search has not indexed yet
        final ContentIds descendants = nodeService.list( ListNodesParams.create().parentPath( nodeToDelete.path() ).build() )
            .map( entry -> ContentId.from( entry.nodeId() ) )
            .collect( ContentIds.collector() );

        final ContentIds unpublishedContents = unpublish( contentId, descendants );
        result.addUnpublished( unpublishedContents );

        final DeleteNodeParams.Builder builder = DeleteNodeParams.create().nodeId( nodeId ).refresh( RefreshMode.SEARCH );

        if ( params.getDeleteContentListener() != null )
        {
            builder.deleteNodeListener( new ListenerDelegate( params.getDeleteContentListener() ) );
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
            .eventPublisher( eventPublisher )
            .params(
                UnpublishContentParams.create().contentIds( ContentIds.create().addAll( descendants ).add( contentId ).build() ).build() )
            .build()
            .execute()
            .getUnpublishedContents();
    }

    static class Builder
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
            requireNonNull( params, "params cannot be null" );
        }

        DeleteContentCommand build()
        {
            validate();
            return new DeleteContentCommand( this );
        }
    }

    private static final class ListenerDelegate
        implements DeleteNodeListener
    {
        private final DeleteContentListener delegate;

        ListenerDelegate( final DeleteContentListener delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public void nodesDeleted( final int count )
        {
            delegate.contentDeleted( count );
        }

        @Override
        public void resolved( final int count )
        {
            delegate.resolved( count );
        }
    }
}
