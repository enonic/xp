package com.enonic.wem.repo.internal;

import java.util.Hashtable;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate = true)
final class ElasticsearchActivator
{
    private Node node;

    private ServiceRegistration<Client> clientReg;

    @Activate
    public void activate( final ComponentContext context )
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
        final Settings settings = new NodeSettingsBuilderImpl().buildSettings();

        this.node = NodeBuilder.nodeBuilder().settings( settings ).build();
        this.node.start();

        final BundleContext bundleContext = context.getBundleContext();
        this.clientReg = bundleContext.registerService( Client.class, this.node.client(), new Hashtable<>() );
    }

    @Deactivate
    public void deactivate()
    {
        this.clientReg.unregister();
        this.node.stop();
    }
}
