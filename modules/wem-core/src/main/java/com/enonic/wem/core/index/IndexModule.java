package com.enonic.wem.core.index;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.index.elastic.ElasticClientFactory;
import com.enonic.wem.core.index.elastic.ElasticNodeFactory;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexServiceImpl;

public final class IndexModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        // TODO: Should be removed
        bind( ResourcePatternResolver.class ).to( PathMatchingResourcePatternResolver.class ).in( Scopes.SINGLETON );

        bind( Node.class ).toProvider( ElasticNodeFactory.class ).in( Scopes.SINGLETON );
        bind( Client.class ).toProvider( ElasticClientFactory.class ).in( Scopes.SINGLETON );
        bind( ElasticsearchIndexService.class ).to( ElasticsearchIndexServiceImpl.class ).in( Scopes.SINGLETON );
        bind( IndexService.class ).in( Scopes.SINGLETON );
    }
}
