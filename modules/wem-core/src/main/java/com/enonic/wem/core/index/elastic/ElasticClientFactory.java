package com.enonic.wem.core.index.elastic;

import javax.inject.Inject;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.lifecycle.DisposableBean;
import com.enonic.wem.core.lifecycle.InitializingBean;

@Component
public final class ElasticClientFactory
    implements FactoryBean<Client>, InitializingBean, DisposableBean
{
    private Node node;

    private Client client;

    @Override
    public Client getObject()
    {
        return this.client;
    }

    @Override
    public Class<?> getObjectType()
    {
        return Client.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
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
