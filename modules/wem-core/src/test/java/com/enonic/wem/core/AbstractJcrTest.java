package com.enonic.wem.core;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class AbstractJcrTest
{
    protected Session session;

    private final JcrTestHelper jcrTestHelper;

    private final SerializingTestHelper serializingTestHelper;

    protected AbstractJcrTest()
    {
        jcrTestHelper = new JcrTestHelper();
        this.session = jcrTestHelper.getSession();
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
        setupDao();
    }

    @After
    public final void afterAbstractJcrTest()
        throws Exception
    {
        jcrTestHelper.destroyMicroKernelFactory();
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
