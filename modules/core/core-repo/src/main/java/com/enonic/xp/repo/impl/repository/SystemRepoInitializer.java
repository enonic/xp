package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.SystemConstants;

public class SystemRepoInitializer
{
    private final RepositoryService repositoryService;

    public SystemRepoInitializer( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    public void initialize()
    {

        final boolean initialized = this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() );

        if ( !initialized )
        {
            this.repositoryService.create( RepositorySettings.create().
                repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                build() );
        }
    }

}
