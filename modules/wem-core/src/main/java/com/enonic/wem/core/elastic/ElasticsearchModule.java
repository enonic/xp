package com.enonic.wem.core.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

import com.google.inject.AbstractModule;

public class ElasticsearchModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( Node.class ).toProvider( ElasticNodeProvider.class );
        bind( Client.class ).toProvider( ElasticClientProvider.class );
    }

}
