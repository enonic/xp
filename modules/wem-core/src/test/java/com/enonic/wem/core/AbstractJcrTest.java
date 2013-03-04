package com.enonic.wem.core;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTimeUtils;
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

    private final TestUtil testUtil;

    protected AbstractJcrTest()
    {
        testUtil = new TestUtil( this ).prettyPrintJson( false );
    }

    protected String getJsonFileAsString( final String fileName )
    {
        return testUtil.getJsonFileAsString( fileName );
    }

    protected JsonNode getJsonFileAsJson( final String fileName )
    {
        return testUtil.parseFileAsJson( fileName );
    }

    protected JsonNode stringToJson( final String jsonString )
    {
        return testUtil.stringToJson( jsonString );
    }

    @Before
    public final void beforeAbstractJcrTest()
        throws Exception
    {
        jcrMicroKernelFactory = new JcrMicroKernelFactory();
        jcrMicroKernelFactory.setInMemoryRepository( true );
        jcrMicroKernelFactory.afterPropertiesSet();

        final JcrRepositoryFactory jcrRepositoryFactory = new JcrRepositoryFactory();
        jcrRepositoryFactory.setMicroKernel( jcrMicroKernelFactory.getObject() );
        jcrRepositoryFactory.afterPropertiesSet();
        final Repository repo = jcrRepositoryFactory.getObject();

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
