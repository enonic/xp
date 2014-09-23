package com.enonic.wem.core.initializer;

import javax.inject.Inject;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.initializer.DataInitializer;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.repository.RepositoryInitializer;

final class StartupInitializerImpl
    implements StartupInitializer
{
    @Inject
    protected RepositoryInitializer repositoryInitializer;

    @Inject
    protected Iterable<DataInitializer> initializers;

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

}
