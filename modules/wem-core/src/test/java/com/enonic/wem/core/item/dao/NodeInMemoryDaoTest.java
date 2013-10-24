package com.enonic.wem.core.item.dao;

public class NodeInMemoryDaoTest
    extends AbstractNodeDaoTest
{

    private NodeInMemoryDao dao = new NodeInMemoryDao();

    @Override
    NodeDao createDao()
    {
        return dao;
    }
}
