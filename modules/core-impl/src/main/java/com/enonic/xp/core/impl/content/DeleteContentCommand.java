package com.enonic.xp.core.impl.content;


import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;


final class DeleteContentCommand
    extends AbstractContentCommand
{
    private final DeleteContentParams params;

    private DeleteContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Contents execute()
    {
        params.validate();

        try
        {
            final Contents deletedContents = doExecute();
            for ( Content deletedContent : deletedContents )
            {
                if ( deletedContent != null )
                {
                    if ( deletedContent.getContentState() == ContentState.PENDING_DELETE )
                    {
                        eventPublisher.publish(
                            ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.PENDING, deletedContent.getPath() ) );
                    }
                    else
                    {
                        eventPublisher.publish(
                            ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.DELETE, deletedContent.getPath() ) );
                    }
                }
            }
            return deletedContents;
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private Contents doExecute()
    {
        //Gets the node to delete
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.params.getContentPath() );
        final Node nodeToDelete = this.nodeService.getByPath( nodePath );

        //Executes the deletion on the node and the sub nodes
        final Set<Node> nodesToDelete = Sets.newLinkedHashSet();
        recursiveDelete( nodeToDelete, nodesToDelete );

        return translator.fromNodes( Nodes.from( nodesToDelete ) );
    }

    private void recursiveDelete( Node nodeToDelete, Set<Node> deletedNodes )
    {
        final CompareStatus status = getCompareStatus( nodeToDelete );

        if ( status == CompareStatus.NEW )
        {
            //If the current node is new, deletes it
            final Node deletedNode = nodeService.deleteByPath( nodeToDelete.path() );
            deletedNodes.add( deletedNode );
        }
        else
        {
            //Else, marks the current node as PENDING_DELETE
            final SetNodeStateParams setNodeStateParams = SetNodeStateParams.create().
                nodeId( nodeToDelete.id() ).
                nodeState( NodeState.PENDING_DELETE ).
                build();
            final SetNodeStateResult setNodeStateResult = this.nodeService.setNodeState( setNodeStateParams );
            deletedNodes.addAll( setNodeStateResult.getUpdatedNodes().getSet() );

            //Recursive call for the children
            if ( nodeToDelete.getHasChildren() )
            {
                final FindNodesByParentParams findNodesByParentParams = FindNodesByParentParams.create().
                    parentPath( nodeToDelete.path() ).
                    build();
                final FindNodesByParentResult findNodesByParentResult = this.nodeService.findByParent( findNodesByParentParams );

                for ( Node childNodeToDelete : findNodesByParentResult.getNodes() )
                {
                    recursiveDelete( childNodeToDelete, deletedNodes );
                }
            }
        }
    }

    private CompareStatus getCompareStatus( final Node nodeToDelete )
    {
        final Context context = ContextAccessor.current();
        final Branch currentBranch = context.getBranch();

        final NodeComparison compare;
        if ( currentBranch.equals( ContentConstants.BRANCH_DRAFT ) )
        {
            compare = this.nodeService.compare( nodeToDelete.id(), ContentConstants.BRANCH_MASTER );
        }
        else
        {
            compare = this.nodeService.compare( nodeToDelete.id(), ContentConstants.BRANCH_DRAFT );
        }
        return compare.getCompareStatus();
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
