package com.enonic.xp.system;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;

public class SystemRepoInitializer
{
    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    public SystemRepoInitializer( final NodeService nodeService, final RepositoryService repositoryService )
    {
        this.nodeService = nodeService;
        this.repositoryService = repositoryService;
    }

    public Node initialize()
    {
        final boolean initialized = this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() );

        if ( !initialized )
        {
            this.repositoryService.create( RepositorySettings.create().
                repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                build() );
        }

        final Node existingRoot = this.nodeService.getRoot();

        if ( existingRoot == null )
        {
            final Node rootNode = this.nodeService.createRootNode( CreateRootNodeParams.create().
                childOrder( ChildOrder.from( "_name ASC" ) ).
                permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
                build() );

            nodeService.push( NodeIds.from( rootNode.id() ), SecurityConstants.BRANCH_SECURITY );

            return rootNode;
        }

        return existingRoot;
    }

}
