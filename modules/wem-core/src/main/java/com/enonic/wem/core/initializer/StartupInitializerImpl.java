package com.enonic.wem.core.initializer;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.initializer.DataInitializer;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.repository.RepositoryInitializer;
import com.enonic.wem.core.security.SecurityInitializer;

public final class StartupInitializerImpl
    implements StartupInitializer
{
    private RepositoryInitializer repositoryInitializer;

    private SecurityInitializer securityInitializer;

    private Iterable<DataInitializer> initializers;

    public void cleanData()
        throws Exception
    {
        initializeRepositories( true );
        initializeSecurity();
    }

    public void start()
    {
        initializeRepositories( false );
        initializeSecurity();
    }

    public void initializeData()
        throws Exception
    {
        for ( final DataInitializer initializer : this.initializers )
        {
            initializer.initialize();
        }
    }

    private void initializeRepositories( final boolean reinit )
    {
        final Repository contentRepo = ContentConstants.CONTENT_REPO;

        if ( reinit || !repositoryInitializer.isInitialized( contentRepo ) )
        {
            repositoryInitializer.init( contentRepo );
        }
    }

    private void initializeSecurity()
    {
        securityInitializer.init();
    }

    public void setRepositoryInitializer( final RepositoryInitializer repositoryInitializer )
    {
        this.repositoryInitializer = repositoryInitializer;
    }

    public void setInitializers( final Iterable<DataInitializer> initializers )
    {
        this.initializers = initializers;
    }

    public void setSecurityInitializer( final SecurityInitializer securityInitializer )
    {
        this.securityInitializer = securityInitializer;
    }
}
