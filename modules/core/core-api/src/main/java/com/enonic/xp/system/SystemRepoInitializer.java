package com.enonic.xp.system;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;

public class SystemRepoInitializer
{
    private final NodeService nodeService;

    public SystemRepoInitializer( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public Node initialize()
    {
        final Node existingRoot = this.nodeService.getRoot();

        if ( existingRoot == null )
        {
            final Node rootNode = this.nodeService.createRootNode( CreateRootNodeParams.create().
                childOrder( ChildOrder.from( "_name ASC" ) ).
                permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
                build() );

            nodeService.push( NodeIds.from( rootNode.id() ), SecurityConstants.BRANCH_ID_SECURITY );

            return rootNode;
        }

        return existingRoot;
    }

}
