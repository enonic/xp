package com.enonic.wem.core.index.elastic;

import javax.inject.Inject;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

import com.google.inject.Provider;

import com.enonic.wem.core.lifecycle.DisposableBean;
import com.enonic.wem.core.lifecycle.InitializingBean;


public final class ElasticClientFactory
    implements Provider<Client>, InitializingBean, DisposableBean
{
    private Node node;

    private Client client;

    @Override
    public Client get()
    {
        return this.client;
    }

    @Inject
    public void setNode( final Node node )
    {
        this.node = node;
    }

    @Override
    public void destroy()
        throws Exception
    {
        this.client.close();
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        this.client = this.node.client();
    }
}
