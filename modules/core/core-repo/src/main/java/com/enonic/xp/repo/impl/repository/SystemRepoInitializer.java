package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.context.ContextBuilder;
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

    private final RepositoryService repositoryService;

    public SystemRepoInitializer( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    public void initialize()
    {
        runAsAdmin( () -> {
            final boolean initialized = this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() );
            if ( !initialized )
            {
                this.repositoryService.createRepository( RepositorySettings.create().
                    repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                    build() );
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
