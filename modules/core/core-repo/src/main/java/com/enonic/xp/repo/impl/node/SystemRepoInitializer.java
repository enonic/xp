package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SystemRepoInitializer
{
    public static final PrincipalKey SUPER_USER = PrincipalKey.ofUser( UserStoreKey.system(), "su" );

    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    public SystemRepoInitializer( final NodeService nodeService, final RepositoryService repositoryService )
    {
        this.nodeService = nodeService;
        this.repositoryService = repositoryService;
    }

    public void initialize()
    {
        runAsAdmin( () -> {

            final boolean initialized = this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() );
            if ( !initialized )
            {
                this.nodeService.createRepository( RepositorySettings.create().
                    repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                    build() );
            }

            Node rootNode = this.nodeService.getRoot();
            if ( rootNode == null )
            {
                rootNode = this.nodeService.createRootNode( CreateRootNodeParams.create().
                    childOrder( ChildOrder.from( "_name ASC" ) ).
                    permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
                    build() );

                nodeService.push( NodeIds.from( rootNode.id() ), SecurityConstants.BRANCH_SECURITY );
            }
        } );
    }

    private void runAsAdmin( Runnable runnable )
    {
        final User admin = User.create().key( SUPER_USER ).login( "su" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).authInfo( authInfo ).build().runWith( runnable );
    }
}
