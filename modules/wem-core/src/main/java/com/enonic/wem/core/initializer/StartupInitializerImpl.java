package com.enonic.wem.core.initializer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.repository.RepositoryInitializer;

final class StartupInitializerImpl
    implements StartupInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( StartupInitializerImpl.class );

    @Inject
    protected IndexService indexService;

    @Inject
    protected ContentService contentService;

    @Inject
    protected RepositoryInitializer repositoryInitializer;


    @PostConstruct
    public void start()
        throws Exception
    {
        initialize( false );
    }

    public void initialize( final boolean reinit )
        throws Exception
    {
        initializeRespositories( reinit );
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
