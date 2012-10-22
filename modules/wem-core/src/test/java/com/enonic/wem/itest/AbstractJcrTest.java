package com.enonic.wem.itest;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.oak.jcr.RepositoryImpl;
import org.junit.Before;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.jcr.provider.JcrSessionProviderImpl;

public abstract class AbstractJcrTest
    extends AbstractSpringTest
{

    protected Session session;

    @Before
    public final void before()
        throws Exception
    {
        final RepositoryImpl repo = new RepositoryImpl();

        final JcrSessionProviderImpl sessionProvider = new JcrSessionProviderImpl();
        sessionProvider.setRepository( repo );

        final JcrInitializer initializer = new JcrInitializer( sessionProvider );
        initializer.initialize();

        session = sessionProvider.loginAdmin();

        setupDao();
    }

    protected abstract void setupDao()
        throws Exception;

    protected void commit()
        throws RepositoryException
    {
        session.save();
    }
}
