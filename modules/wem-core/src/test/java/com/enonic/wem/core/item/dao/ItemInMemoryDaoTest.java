package com.enonic.wem.core.item.dao;

import org.junit.Ignore;

@Ignore
public class ItemInMemoryDaoTest
    extends AbstractItemDaoTest
{

    private ItemInMemoryDao dao = new ItemInMemoryDao();

    @Override
    ItemDao createDao()
    {
        return dao;
    }
}
