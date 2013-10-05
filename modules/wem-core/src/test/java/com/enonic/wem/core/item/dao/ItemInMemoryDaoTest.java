package com.enonic.wem.core.item.dao;

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
