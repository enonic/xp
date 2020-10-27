package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RenameNodeParams;

public final class RenameNodeCommand
    extends AbstractNodeCommand
{
    private final RenameNodeParams params;

    private RenameNodeCommand( Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public MoveNodeResult execute()
    {
        final NodeId nodeId = params.getNodeId();

        final Node nodeToBeRenamed = doGetById( nodeId );

        if ( nodeToBeRenamed == null )
        {
            throw new NodeNotFoundException( "cannot rename node with id [" + nodeId + "]" );
        }

        if ( nodeToBeRenamed.isRoot() )
        {
            throw new OperationNotPermittedException( "Not allowed to rename root-node" );
        }

        final NodePath parentPath = verifyNodeNotExistAtNewPath( nodeToBeRenamed );

        return MoveNodeCommand.create( this ).
            id( params.getNodeId() ).
            newParent( parentPath ).
            newNodeName( params.getNewNodeName() ).
            processor( params.getProcessor() ).
            build().
            execute();
    }

    private NodePath verifyNodeNotExistAtNewPath( final Node nodeToBeRenamed )
    {
        final NodePath parentPath = nodeToBeRenamed.parentPath().asAbsolute();
        final NodePath targetPath = new NodePath( parentPath, params.getNewNodeName() );
        final Node existingNodeAtTargetPath = GetNodeByPathCommand.create( this ).
            nodePath( targetPath ).
            build().
            execute();

        if ( ( existingNodeAtTargetPath != null ) && !nodeToBeRenamed.id().equals( existingNodeAtTargetPath.id() ) )
        {
            throw new NodeAlreadyExistAtPathException( targetPath );
        }

        return parentPath;
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        Builder()
        {
            super();
        }

        private RenameNodeParams params;

        public Builder params( RenameNodeParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
        }

        public RenameNodeCommand build()
        {
            this.validate();
            return new RenameNodeCommand( this );
        }

    }
}
