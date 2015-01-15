package com.enonic.wem.core.initializer;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.initializer.DataInitializer;
import com.enonic.wem.api.initializer.RepositoryInitializer;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.security.SecurityInitializer;
import com.enonic.wem.api.security.SystemConstants;
import com.enonic.wem.core.content.ContentInitializer;

@Component(immediate = true)
public final class StartupInitializerImpl
    implements StartupInitializer
{
    private RepositoryInitializer repositoryInitializer;

    private SecurityInitializer securityInitializer;

    private ContentInitializer contentInitializer;

    private final List<DataInitializer> initializers;

    public StartupInitializerImpl()
    {
        this.initializers = Lists.newArrayList();
    }

    public void cleanData()
        throws Exception
    {
        initializeRepositories( true );
        initializeSecurity();
        initializeContent();
    }

    @Activate
    public void start()
    {
        initializeRepositories( false );
        initializeSecurity();
        initializeContent();
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
        initializeRepository( ContentConstants.CONTENT_REPO, reinit );
        initializeRepository( SystemConstants.SYSTEM_REPO, reinit );
    }

    private void initializeRepository( final Repository repository, final boolean reInit )
    {
        if ( reInit || !repositoryInitializer.isInitialized( repository ) )
        {
            repositoryInitializer.init( repository );
        }
        else
        {
            repositoryInitializer.waitForInitialized( repository );
        }
    }

    private void initializeContent()
    {
        this.contentInitializer.init();
    }

    private void initializeSecurity()
    {
        securityInitializer.init();
    }

    @Reference
    public void setRepositoryInitializer( final RepositoryInitializer repositoryInitializer )
    {
        this.repositoryInitializer = repositoryInitializer;
    }

    @Reference
    public void setSecurityInitializer( final SecurityInitializer securityInitializer )
    {
        this.securityInitializer = securityInitializer;
    }

    @Reference
    public void setContentInitializer( final ContentInitializer contentInitializer )
    {
        this.contentInitializer = contentInitializer;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addInitializer( final DataInitializer initializer )
    {
        this.initializers.add( initializer );
    }

    public void removeInitializer( final DataInitializer initializer )
    {
        this.initializers.remove( initializer );
    }
}
