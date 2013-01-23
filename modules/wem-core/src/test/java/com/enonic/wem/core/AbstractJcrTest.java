package com.enonic.wem.core;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.After;
import org.junit.Before;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.jcr.provider.JcrSessionProviderImpl;
import com.enonic.wem.core.jcr.repository.JcrMicroKernelFactory;
import com.enonic.wem.core.jcr.repository.JcrRepositoryFactory;

public abstract class AbstractJcrTest
{
    protected Session session;

    private JcrMicroKernelFactory jcrMicroKernelFactory;

    @Before
    public final void before()
        throws Exception
    {
        jcrMicroKernelFactory = new JcrMicroKernelFactory();
        jcrMicroKernelFactory.setInMemoryRepository( true );
        jcrMicroKernelFactory.init();

        final JcrRepositoryFactory jcrRepositoryFactory = new JcrRepositoryFactory();
        jcrRepositoryFactory.setMicroKernel( jcrMicroKernelFactory.getObject() );
        jcrRepositoryFactory.init();
        final Repository repo = jcrRepositoryFactory.getObject();

        final JcrSessionProviderImpl sessionProvider = new JcrSessionProviderImpl();
        sessionProvider.setRepository( repo );

        final JcrInitializer initializer = new JcrInitializer( sessionProvider );
        initializer.initialize();

        session = sessionProvider.loginAdmin();

        setupDao();
    }

    @After
    public final void after()
    {
        jcrMicroKernelFactory.dispose();
    }

    protected abstract void setupDao()
        throws Exception;

    protected void commit()
        throws RepositoryException
    {
        session.save();
    }
}
