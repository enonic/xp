package com.enonic.xp.web.session.impl;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterConfig;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.sessionstore")
public class WebSessionStoreConfigServiceImpl
    implements WebSessionStoreConfigService
{
    private static final Logger LOG = LoggerFactory.getLogger( WebSessionStoreConfigServiceImpl.class );

    public static final String REPLICATED_STORE_MODE = "replicated";

    public static final String NON_PERSISTENT_STORE_MODE = "non-persistent";

    private final ClusterConfig clusterConfig;

    private final WebSessionStoreConfig webSessionstoreConfig;

    private String enabledComponent;

    @Activate
    public WebSessionStoreConfigServiceImpl( final WebSessionStoreConfig webSessionstoreConfig,
                                             @Reference final ClusterConfig clusterConfig )
    {
        this.webSessionstoreConfig = webSessionstoreConfig;
        this.clusterConfig = clusterConfig;
    }

    @Activate
    public void activate( final ComponentContext context )
    {
        final String storeMode = webSessionstoreConfig.storeMode();
        final String enableComponent;
        if ( clusterConfig.isEnabled() && REPLICATED_STORE_MODE.equals( storeMode ) )
        {
            enableComponent = HazelcastSessionStoreFactoryActivator.class.getName();
        }
        else
        {
            if ( !NON_PERSISTENT_STORE_MODE.equals( storeMode ) )
            {
                LOG.warn( "Session storeMode '{}' requires cluster to be enabled. Fall back to '{}'", REPLICATED_STORE_MODE,
                          NON_PERSISTENT_STORE_MODE );
            }
            enableComponent = NullSessionStoreFactoryActivator.class.getName();
        }
        context.enableComponent( enableComponent );
        enabledComponent = enableComponent;
    }

    @Deactivate
    public void deactivate( final ComponentContext context )
    {
        context.disableComponent( enabledComponent );
    }

    @Override
    public int getSavePeriodSeconds()
    {
        return webSessionstoreConfig.savePeriodSeconds();
    }

    @Override
    public int getGracePeriodSeconds()
    {
        return webSessionstoreConfig.gracePeriodSeconds();
    }

    @Override
    public boolean isSaveOnCreate()
    {
        return webSessionstoreConfig.saveOnCreate();
    }

    @Override
    public boolean isFlushOnResponseCommit()
    {
        return webSessionstoreConfig.flushOnResponseCommit();
    }
}
