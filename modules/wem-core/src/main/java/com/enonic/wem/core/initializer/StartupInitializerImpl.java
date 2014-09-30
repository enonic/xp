package com.enonic.wem.core.initializer;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.initializer.DataInitializer;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.repository.RepositoryInitializer;

public final class StartupInitializerImpl
    implements StartupInitializer
{
    private RepositoryInitializer repositoryInitializer;

    private Iterable<DataInitializer> initializers;

    public void cleanData()
        throws Exception
    {
        initializeRespositories( true );
    }

    public void initializeData()
        throws Exception
    {
        for ( final DataInitializer initializer : this.initializers )
        {
            initializer.initialize();
        }
    }

    private void initializeRespositories( final boolean reinit )
    {
        final Repository contentRepo = ContentConstants.CONTENT_REPO;

        if ( reinit || !repositoryInitializer.isInitialized( contentRepo ) )
        {
            repositoryInitializer.init( contentRepo );
        }
    }

    public void setRepositoryInitializer( final RepositoryInitializer repositoryInitializer )
    {
        this.repositoryInitializer = repositoryInitializer;
    }

    public void setInitializers( final Iterable<DataInitializer> initializers )
    {
        this.initializers = initializers;
    }
}
