package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class DeleteNodeInChildBranchCommand
    extends AbstractChildBranchCommand
{
    private NodeId nodeId;

    private NodePath nodePath;

    private DeleteNodeInChildBranchCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.nodePath = builder.nodePath;
    }

    @Override
    protected void execute( final Branch parentBranch, final Branches childBranches )
    {
        if ( nodeId == null )
        {
            execute( childBranches, nodePath );
        }
        else
        {
            execute( childBranches, nodeId );
        }
    }

    private void execute( final Branches childBranches, final NodeId nodeId )
    {
        if ( childBranches.isEmpty() )
        {
            return;
        }
        for ( Branch childBranch : childBranches )
        {
            runInBranch( childBranch, () -> {
                DeleteNodeByIdCommand.create().
                    nodeId( nodeId ).
                    indexServiceInternal( this.indexServiceInternal ).
                    storageService( this.nodeStorageService ).
                    searchService( this.nodeSearchService ).
                    build().
                    execute();
                execute( getChildBranches( childBranch ), nodeId );
            } );
        }
    }

    private void execute( final Branches childBranches, final NodePath nodePath )
    {
        if ( childBranches.isEmpty() )
        {
            return;
        }
        for ( Branch childBranch : childBranches )
        {
            runInBranch( childBranch, () -> {
                DeleteNodeByPathCommand.create().
                    nodePath( nodePath ).
                    indexServiceInternal( this.indexServiceInternal ).
                    storageService( this.nodeStorageService ).
                    searchService( this.nodeSearchService ).
                    build().
                    execute();
                execute( getChildBranches( childBranch ), nodePath );
            } );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractChildBranchCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private NodePath nodePath;

        Builder()
        {
            super();
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeId == null ? nodePath : nodeId );
        }

        public DeleteNodeInChildBranchCommand build()
        {
            validate();
            return new DeleteNodeInChildBranchCommand( this );
        }
    }
}
