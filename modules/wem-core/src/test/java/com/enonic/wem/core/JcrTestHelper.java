package com.enonic.wem.core;


import javax.jcr.Repository;
import javax.jcr.Session;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.jcr.provider.JcrSessionProviderImpl;
import com.enonic.wem.core.jcr.repository.JcrMicroKernelFactory;
import com.enonic.wem.core.jcr.repository.JcrRepositoryFactory;

public class JcrTestHelper
{
    private final Session session;

    private final JcrMicroKernelFactory jcrMicroKernelFactory;

    public JcrTestHelper()
    {
        try
        {
            jcrMicroKernelFactory = new JcrMicroKernelFactory();
            jcrMicroKernelFactory.setInMemoryRepository( true );
            jcrMicroKernelFactory.afterPropertiesSet();

            final JcrRepositoryFactory jcrRepositoryFactory = new JcrRepositoryFactory();
            jcrRepositoryFactory.setMicroKernel( jcrMicroKernelFactory.get() );
            jcrRepositoryFactory.afterPropertiesSet();
            final Repository repo = jcrRepositoryFactory.get();

            final JcrSessionProviderImpl sessionProvider = new JcrSessionProviderImpl();
            sessionProvider.setRepository( repo );

            final JcrInitializer initializer = new JcrInitializer( sessionProvider );
            initializer.initialize();

            session = sessionProvider.loginAdmin();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to construct JcrTestHelper", e );
        }
    }

    public Session getSession()
    {
        return session;
    }

    public void destroyMicroKernelFactory()
        throws Exception
    {
        this.jcrMicroKernelFactory.destroy();
    }
}
