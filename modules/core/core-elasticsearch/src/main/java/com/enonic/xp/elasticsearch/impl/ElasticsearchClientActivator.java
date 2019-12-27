package com.enonic.xp.elasticsearch.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ConnectException;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.elasticsearch.client.impl.EsClient;

@Component(immediate = true, configurationPid = "com.enonic.xp.elasticsearch")
public final class ElasticsearchClientActivator
{

    private static final Logger LOG = LoggerFactory.getLogger( ElasticsearchClientActivator.class );

    private EsClient client;

    private ServiceRegistration<EsClient> clientReg;

    private BundleContext context;

    private ScheduledExecutorService activateExecutorService;

    private volatile boolean isRegistered = false;

    @Activate
    @SuppressWarnings("WeakerAccess")
    public void activate( final BundleContext context )
        throws InterruptedException
    {
        this.context = context;
        this.client = new EsClient( "localhost", 9200 );

        doActivate();
    }

    private void doActivate()
        throws InterruptedException
    {
        final CountDownLatch activateLatch = new CountDownLatch( 1 );
        this.activateExecutorService = Executors.newSingleThreadScheduledExecutor();

        try
        {
            activateExecutorService.scheduleWithFixedDelay( () -> {
                try
                {
                    if ( !isRegistered )
                    {
                        doRegisterElasticsearchClient();
                    }
                    else
                    {
                        doMonitorElasticsearchHealth();
                    }
                }
                catch ( Exception e )
                {
                    handleException( e );
                }
                finally
                {
                    activateLatch.countDown();
                }
            }, 0, 1, TimeUnit.SECONDS );

            activateLatch.await();
        }
        catch ( Exception e )
        {
            activateExecutorService.shutdown();
            throw e;
        }
    }

    private void doRegisterElasticsearchClient()
        throws IOException
    {
        client.ping();

        final ClusterHealthResponse healthResponse = client.clusterHealth( new ClusterHealthRequest() );

        if ( healthResponse.getStatus() != ClusterHealthStatus.RED )
        {
            register();

            LOG.info( "Elasticsearch is up." );
        }
    }

    private void doMonitorElasticsearchHealth()
    {
        try
        {
            boolean pingSucceeded = client.ping();

            final ClusterHealthResponse healthResponse = client.clusterHealth( new ClusterHealthRequest() );

            if ( !pingSucceeded || healthResponse.getStatus() == ClusterHealthStatus.RED )
            {
                unregister();
            }
        }
        catch ( final Exception e )
        {
            unregister();

            throw e;
        }
    }

    @Deactivate
    @SuppressWarnings("WeakerAccess")
    public void deactivate()
        throws IOException
    {
        activateExecutorService.shutdown();

        unregister();

        if ( client != null )
        {
            client.close();
        }
    }

    private void register()
    {
        if ( this.clientReg != null )
        {
            return;
        }

        this.clientReg = context.registerService( EsClient.class, client, new Hashtable<>() );
        this.isRegistered = true;
    }

    private void unregister()
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
            this.isRegistered = false;
            this.clientReg = null;
        }
    }

    private void handleException( final Exception e )
    {
        if ( e instanceof UncheckedIOException )
        {
            LOG.error( "Error while checking Elasticsearch healthy. Connection refused for RestHighLevelClient." );
        }
        else
        {
            LOG.error( "Error while checking Elasticsearch healthy", e );
        }
    }

}

