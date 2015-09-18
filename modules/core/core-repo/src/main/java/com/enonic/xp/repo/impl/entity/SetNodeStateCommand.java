package com.enonic.xp.repo.impl.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;
import com.enonic.xp.repo.impl.index.query.QueryService;

public class SetNodeStateCommand
    extends AbstractNodeCommand
{
    private final SetNodeStateParams params;

    private SetNodeStateCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public SetNodeStateResult execute()
    {
        final Node node = doGetById( this.params.getNodeId(), false );

        final SetNodeStateResult.Builder setNodeStateResultBuilder = SetNodeStateResult.create();
        if( this.params.isRecursive() )
        {
            setNodeStateWithChildren(node, this.params.getNodeState(), setNodeStateResultBuilder);
        } else
        {
            setNodeState(node, this.params.getNodeState(), setNodeStateResultBuilder);
        }

        return setNodeStateResultBuilder.build();
    }

    private Node setNodeState( final Node node, final NodeState nodeState, final SetNodeStateResult.Builder setNodeStateResultBuilder)
    {
        final Node updatedNode = Node.create( node ).
            nodeState( this.params.getNodeState() ).
            build();

        updateNodeMetadata( updatedNode );

        setNodeStateResultBuilder.addUpdatedNode( updatedNode );

        return updatedNode;
    }


    private Node setNodeStateWithChildren( final Node node, final NodeState nodeState,
                                           final SetNodeStateResult.Builder setNodeStateResultBuilder )
    {
        //Updates the current node state
        final Node updatedNode = setNodeState( node, nodeState, setNodeStateResultBuilder );


        //Finds the children
        FindNodesByParentParams findNodesByParentParams = FindNodesByParentParams.create().parentPath( node.path() ).size(
            QueryService.GET_ALL_SIZE_FLAG ).build();
        final FindNodesByParentResult result = doFindNodesByParent( findNodesByParentParams );

        //Updates the children state
        for ( final Node child : result.getNodes() )
        {
            setNodeStateWithChildren( child, nodeState, setNodeStateResultBuilder );
        }

        return updatedNode;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private SetNodeStateParams params;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( final SetNodeStateParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public SetNodeStateCommand build()
        {
            this.validate();
            return new SetNodeStateCommand( this );
        }
    }
}
