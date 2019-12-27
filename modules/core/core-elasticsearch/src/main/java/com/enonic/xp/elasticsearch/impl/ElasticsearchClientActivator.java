package com.enonic.xp.elasticsearch.impl;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, configurationPid = "com.enonic.xp.elasticsearch")
public final class ElasticsearchClientActivator
    implements ClientActivator
{

    private static final Logger LOG = LoggerFactory.getLogger( ElasticsearchClientActivator.class );

    private RestHighLevelClient client;

    private ServiceRegistration<RestHighLevelClient> clientReg;

    private BundleContext context;

    @SuppressWarnings("WeakerAccess")
    public ElasticsearchClientActivator()
    {
    }

    @Activate
    @SuppressWarnings("WeakerAccess")
    public void activate( final BundleContext context )
    {
        this.context = context;

        this.client = new RestHighLevelClient( RestClient.builder( new HttpHost( "localhost", 9200, "http" ) ) );

        while ( true )
        {
            try
            {
                if ( client.ping( RequestOptions.DEFAULT ) )
                {
                    final ClusterHealthResponse healthResponse =
                        client.cluster().health( new ClusterHealthRequest(), RequestOptions.DEFAULT );

                    if ( healthResponse.getStatus() == ClusterHealthStatus.RED )
                    {
                        LOG.info( "Elasticsearch health status is RED." );
                    }
                    else
                    {
                        register();

                        LOG.info( "Elasticsearch is up." );

                        break;
                    }
                }
            }
            catch ( final IOException e )
            {
                LOG.info( "Checking if Elasticsearch is up." );

                try
                {
                    Thread.sleep( 1000 );
                }
                catch ( InterruptedException ex )
                {
                    // do nothing
                }
            }
        }
    }

    @Deactivate
    @SuppressWarnings("WeakerAccess")
    public void deactivate()
        throws IOException
    {
        unregister();

        if ( client != null )
        {
            client.close();
        }
    }

    @Override
    public void register()
    {
        if ( this.clientReg != null )
        {
            return;
        }

        this.clientReg = context.registerService( RestHighLevelClient.class, client, new Hashtable<>() );
    }

    @Override
    public void unregister()
    {
        if ( this.clientReg == null )
        {
            return;
        }

        try
        {
            this.clientReg.unregister();
        }
        finally
        {
            this.clientReg = null;
        }
    }
}

