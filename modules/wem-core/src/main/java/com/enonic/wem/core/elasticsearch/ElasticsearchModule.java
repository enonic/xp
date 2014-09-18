package com.enonic.wem.core.elasticsearch;

import javax.inject.Singleton;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.elasticsearch.workspace.ElasticsearchWorkspaceService;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;

public class ElasticsearchModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( Node.class ).toProvider( ElasticNodeProvider.class );
        bind( Client.class ).toProvider( ElasticClientProvider.class );
        bind( VersionService.class ).to( ElasticsearchVersionService.class ).in( Singleton.class );
        bind( QueryService.class ).to( ElasticsearchQueryService.class ).in( Singleton.class );
        bind( IndexService.class ).to( ElasticsearchIndexService.class ).in( Singleton.class );
        bind( WorkspaceService.class ).to( ElasticsearchWorkspaceService.class ).in( Singleton.class );
    }

}
