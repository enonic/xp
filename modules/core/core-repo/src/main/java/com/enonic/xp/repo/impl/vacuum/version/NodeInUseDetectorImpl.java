package com.enonic.xp.repo.impl.vacuum.version;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true)
public class NodeInUseDetectorImpl
    implements NodeInUseDetector
{
    private NodeService nodeService;

    private RepositoryService repositoryService;

    public boolean execute( final NodeId nodeId )
    {
        for ( final Repository repository : this.repositoryService.list() )
        {
            for ( final Branch branch : repository.getBranches() )
            {
                try
                {
                    ContextBuilder.from( ContextAccessor.current() ).
                        branch( branch ).
                        repositoryId( repository.getId() ).
                        build().callWith( () -> this.nodeService.getById( nodeId ) );
                    return true;
                }
                catch ( NodeNotFoundException e )
                {
                    // Ignore
                }
            }
        }

        return false;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
