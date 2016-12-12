package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SystemRepoInitializer
{
    private static final PrincipalKey SUPER_USER = PrincipalKey.ofUser( UserStoreKey.system(), "su" );

    private final RepositoryService repositoryService;

    public SystemRepoInitializer( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    public void initialize()
    {
        runAsAdmin( () ->
                    {
                        final boolean initialized = this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() );
                        if ( !initialized )
                        {
                            final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
                                repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                                rootChildOrder( ChildOrder.from( "_name ASC" ) ).
                                rootPermissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
                                build();

                            this.repositoryService.createRepository( createRepositoryParams );
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
