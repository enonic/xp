package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.core.node.NodeId;
import com.enonic.xp.core.node.NodePath;
import com.enonic.xp.core.node.RenameNodeParams;

public final class RenameNodeCommand
    extends AbstractNodeCommand
{
    private final RenameNodeParams params;

    private RenameNodeCommand( Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public Node execute()
    {
        final NodeId nodeId = params.getNodeId();

        final Node nodeToBeRenamed = doGetById( nodeId, true );

        final NodePath parentPath = verifyNodeNotExistAtNewPath( nodeToBeRenamed );

        final Node renamedNode = doMoveNode( parentPath, params.getNewNodeName(), params.getNodeId() );

        return NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( renamedNode );
    }

    private NodePath verifyNodeNotExistAtNewPath( final Node nodeToBeRenamed )
    {
        final NodePath parentPath = nodeToBeRenamed.parentPath().asAbsolute();
        final NodePath targetPath = new NodePath( parentPath, params.getNewNodeName() );
        final Node existingNodeAtTargetPath = doGetByPath( targetPath, false );

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
