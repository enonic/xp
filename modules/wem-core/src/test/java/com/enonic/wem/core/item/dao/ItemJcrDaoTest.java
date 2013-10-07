package com.enonic.wem.core.item.dao;

import org.joda.time.DateTimeUtils;
import org.junit.After;

import com.enonic.wem.core.JcrTestHelper;


public class ItemJcrDaoTest
    extends AbstractItemDaoTest
{
    private final JcrTestHelper jcrTestHelper;

    private final ItemJcrDao dao;

    public ItemJcrDaoTest()
    {
        jcrTestHelper = new JcrTestHelper();
        dao = new ItemJcrDao( jcrTestHelper.getSession() );
    }

    @After
    public final void after()
        throws Exception
    {
        jcrTestHelper.destroyMicroKernelFactory();
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Override
    ItemDao createDao()
    {
        return dao;
    }
}
