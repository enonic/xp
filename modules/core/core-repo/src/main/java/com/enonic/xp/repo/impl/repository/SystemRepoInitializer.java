package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SystemRepoInitializer
{
    private static final PrincipalKey SUPER_USER = PrincipalKey.ofSuperUser();

    private final RepositoryService repositoryService;

    private NodeStorageService nodeStorageService;

    public SystemRepoInitializer( final RepositoryService repositoryService, final NodeStorageService nodeStorageService )
    {
        this.repositoryService = repositoryService;
        this.nodeStorageService = nodeStorageService;
    }

    public void initialize()
    {
        runAsAdmin( () -> {
            final boolean initialized = this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() );
            if ( !initialized )
            {
                final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
                    repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                    rootChildOrder( ChildOrder.from( "_name ASC" ) ).
                    rootPermissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
                    build();

                this.repositoryService.createRepository( createRepositoryParams );

                initRepositoryFolder();
            }
        } );
    }

    private void initRepositoryFolder()
    {
        final Context currentContext = ContextAccessor.current();
        final Node node = Node.create( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getParentPath() ).
            name( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getName() ).
            permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
            build();

        this.nodeStorageService.store( node, InternalContext.from( currentContext ) );
    }

    private void runAsAdmin( Runnable runnable )
    {
        final User admin = User.create().key( SUPER_USER ).login( SUPER_USER.getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).authInfo( authInfo ).build().runWith( runnable );
    }
}
