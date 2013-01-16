package com.enonic.wem.core.search.elastic;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ElasticClientFactory
    implements FactoryBean<Client>
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

    @Autowired
    public void setNode( final Node node )
    {
        this.node = node;
    }

    @PostConstruct
    public void start()
    {
        this.client = this.node.client();
    }

    @PreDestroy
    public void stop()
    {
        this.client.close();
    }
}
