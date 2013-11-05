package com.enonic.wem.core.entity.dao;

import org.joda.time.DateTimeUtils;
import org.junit.After;

import com.enonic.wem.core.JcrTestHelper;


public class NodeJcrDaoTest
    extends AbstractNodeDaoTest
{
    private final JcrTestHelper jcrTestHelper;

    private final NodeJcrDao dao;

    public NodeJcrDaoTest()
    {
        jcrTestHelper = new JcrTestHelper();
        dao = new NodeJcrDao( jcrTestHelper.getSession() );
    }

    @After
    public final void after()
        throws Exception
    {
        jcrTestHelper.destroyMicroKernelFactory();
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Override
    NodeDao createDao()
    {
        return dao;
    }
}
