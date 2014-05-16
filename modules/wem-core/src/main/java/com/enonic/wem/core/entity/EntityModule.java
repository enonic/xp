package com.enonic.wem.core.entity;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.elasticsearch.ElasticsearchWorkspaceStore;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeDaoImpl;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.workspace.WorkspaceStore;

public final class EntityModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( NodeService.class ).to( NodeServiceImpl.class ).in( Singleton.class );
        bind( NodeDao.class ).to( NodeDaoImpl.class ).in( Singleton.class );
        bind( IndexService.class ).to( ElasticsearchIndexService.class ).in( Singleton.class );
        bind( WorkspaceStore.class ).to( ElasticsearchWorkspaceStore.class ).in( Singleton.class );
    }
}
