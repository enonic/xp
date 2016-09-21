package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;

public final class RenameNodeCommand
    extends RepositorySpecificNodeCommand
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

        final NodePath parentPath = nodeToBeRenamed.parentPath().asAbsolute();
        verifyNodeNotExistAtNewPath( parentPath, nodeToBeRenamed.id() );

        return MoveNodeCommand.create( this ).
            id( params.getNodeId() ).
            newParent( parentPath ).
            newNodeName( params.getNewNodeName() ).
            build().
            execute();
    }

    private void verifyNodeNotExistAtNewPath( final NodePath parentPath, final NodeId id )
    {
        if ( skipNodeExistsVerification() )
        {
            return;
        }
        
        final NodePath targetPath = new NodePath( parentPath, params.getNewNodeName() );
        final Node existingNodeAtTargetPath = GetNodeByPathCommand.create( this ).
            nodePath( targetPath ).
            build().
            execute();

        if ( ( existingNodeAtTargetPath != null ) && !id.equals( existingNodeAtTargetPath.id() ) )
        {
            throw new NodeAlreadyExistAtPathException( targetPath );
        }
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends RepositorySpecificNodeCommand.Builder<Builder>
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
