package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionDeleteException;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;

public class DeleteVersionCommand
    extends AbstractNodeCommand
{
    private final NodeVersionId nodeVersionId;

    private final RepositoryService repositoryService;

    private DeleteVersionCommand( final Builder builder )
    {
        super( builder );
        nodeVersionId = builder.nodeVersionId;
        repositoryService = builder.repositoryService;
    }

    public boolean execute()
    {
        NodeHelper.runAsAdmin( this::doExecute );
        return false;
    }

    private void doExecute()
    {
        final NodeVersion nodeVersion = this.nodeStorageService.get( this.nodeVersionId );

        if ( isInUse( nodeVersion.getId() ) )
        {
            throw new NodeVersionDeleteException( "Cannot delete version of a node that is in use" );
        }
    }

    private boolean isInUse( final NodeId nodeId )
    {
        for ( final Repository repo : this.repositoryService.list() )
        {
            for ( final Branch branch : repo.getBranches() )
            {
                if ( nodeIdBranch( nodeId, repo, branch ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean nodeIdBranch( final NodeId nodeId, final Repository repo, final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( branch ).
            repositoryId( repo.getId() ).
            build().callWith( () -> {

            try
            {
                final Node node = doGetById( nodeId );
                return node != null;
            }
            catch ( NodeNotFoundException e )
            {
                // Ignore
            }

            return false;
        } );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeVersionId nodeVersionId;

        private RepositoryService repositoryService;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId val )
        {
            nodeVersionId = val;
            return this;
        }

        public Builder repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public DeleteVersionCommand build()
        {
            return new DeleteVersionCommand( this );
        }
    }
}
