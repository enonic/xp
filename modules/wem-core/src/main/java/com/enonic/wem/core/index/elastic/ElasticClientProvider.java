package com.enonic.wem.core.index.elastic;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

import com.google.inject.Provider;

@Singleton
public final class ElasticClientProvider
    implements Provider<Client>
{
    private Node node;

    @Inject
    public ElasticClientProvider( final Node node )
    {
        this.node = node;
    }

    @Override
    public Client get()
    {
        return this.node.client();
    }
}
