package com.enonic.wem.core.item.dao;

public class ItemInMemoryDaoTest
    extends AbstractItemDaoTest
{

    private NodeInMemoryDao dao = new NodeInMemoryDao();

    @Override
    NodeDao createDao()
    {
        return dao;
    }
}
