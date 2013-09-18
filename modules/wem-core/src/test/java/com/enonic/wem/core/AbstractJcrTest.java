package com.enonic.wem.core;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.jcr.provider.JcrSessionProviderImpl;
import com.enonic.wem.core.jcr.repository.JcrMicroKernelFactory;
import com.enonic.wem.core.jcr.repository.JcrRepositoryFactory;

public abstract class AbstractJcrTest
{
    protected Session session;

    private JcrMicroKernelFactory jcrMicroKernelFactory;

    private final SerializingTestHelper serializingTestHelper;

    protected AbstractJcrTest()
    {
        serializingTestHelper = new SerializingTestHelper( this, false );
    }

    protected JsonNode getJsonFileAsJson( final String fileName )
    {
        return serializingTestHelper.loadTestJson( fileName );
    }

    protected JsonNode stringToJson( final String jsonString )
    {
        return serializingTestHelper.stringToJson( jsonString );
    }

    @Before
    public final void beforeAbstractJcrTest()
        throws Exception
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

        setupDao();
    }

    @After
    public final void afterAbstractJcrTest()
        throws Exception
    {
        jcrMicroKernelFactory.destroy();
        DateTimeUtils.setCurrentMillisSystem();
    }

    protected abstract void setupDao()
        throws Exception;

    protected void commit()
        throws RepositoryException
    {
        session.save();
    }
}
