package com.enonic.xp.core.impl.content;


import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.FindNodesByParentParams;
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
        params.validate();

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

        deleteNodeInDraftAndMaster( nodeToDelete, result );

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

    private void deleteNodeInDraftAndMaster( final NodeId nodeToDelete, final DeleteContentsResult.Builder result )
    {
        final Context draftContext = ContextAccessor.current();
        final Context masterContext = ContextBuilder.from( draftContext ).
            branch( ContentConstants.BRANCH_MASTER ).
            build();

        final Node draftRootNode = nodeService.getById( nodeToDelete );

        final NodeIds draftNodes = deleteNodeInContext( nodeToDelete, draftContext );
        final NodeIds masterNodes = deleteNodeInContext( nodeToDelete, masterContext );

        result.addDeleted( ContentNodeHelper.toContentIds( draftNodes ) );
        result.addUnpublished( ContentNodeHelper.toContentIds( masterNodes ) );

        final NodeIds masterIdsByDraftPath = masterContext.callWith( () ->  // to delete master with moved draft from moved tree
                                                                         this.nodeService.findByParent( FindNodesByParentParams.create().
                                                                             parentPath( draftRootNode.path() ).
                                                                             recursive( true ).
                                                                             build() ).
                                                                             getNodeIds() );

        Stream.concat( masterIdsByDraftPath.stream(), draftNodes.stream() ).
            filter( id -> !masterNodes.contains( id ) ).
            forEach( id -> {
            final NodeIds nodeIds = deleteNodeInContext( id, masterContext );
            result.addUnpublished( ContentNodeHelper.toContentIds( nodeIds ) );
            } );
    }

    private NodeIds deleteNodeInContext( final NodeId nodeToDelete, final Context context )
    {
        return context.callWith( () -> this.nodeService.deleteById( nodeToDelete, this ) );
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
            Preconditions.checkNotNull( params );
        }

        public DeleteContentCommand build()
        {
            validate();
            return new DeleteContentCommand( this );
        }
    }

}
