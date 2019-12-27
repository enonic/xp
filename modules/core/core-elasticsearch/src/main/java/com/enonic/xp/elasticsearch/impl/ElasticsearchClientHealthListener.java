package com.enonic.xp.elasticsearch.impl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, configurationPid = "com.enonic.xp.elasticsearch")
public class ElasticsearchClientHealthListener
{

    private static final Logger LOG = LoggerFactory.getLogger( ElasticsearchClientHealthListener.class );

    private final CopyOnWriteArrayList<ClientActivator> clientActivators = new CopyOnWriteArrayList<>();

    private Timer timer = new Timer();

    private RestHighLevelClient client;

    @Activate
    public void activate( final BundleContext context )
    {
        this.client = new RestHighLevelClient( RestClient.builder( new HttpHost( "localhost", 9200, "http" ) ) );

        this.timer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                checkRestClientState();
            }
        }, 1000, 1000 );
    }

    @Deactivate
    public void deactivate()
    {
        this.timer.cancel();
    }

    private void checkRestClientState()
    {
        try
        {
            boolean pingSucceeded = client.ping( RequestOptions.DEFAULT );

            final ClusterHealthResponse healthResponse = client.cluster().health( new ClusterHealthRequest(), RequestOptions.DEFAULT );

            if ( !pingSucceeded )
            {
                clientActivators.forEach( ClientActivator::unregister );
            }
            else if ( healthResponse.getStatus() == ClusterHealthStatus.RED )
            {
                clientActivators.forEach( ClientActivator::unregister );
            }
            else
            {
                clientActivators.forEach( ClientActivator::register );
            }
        }
        catch ( final IOException e )
        {
            clientActivators.forEach( ClientActivator::unregister );

            LOG.warn( "Error while checking Elasticsearch healthy" );
        }
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void addClientActivator( final ClientActivator clientActivator )
    {
        this.clientActivators.add( clientActivator );
    }

}
