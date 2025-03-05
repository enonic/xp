package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.hazelcast.session.HazelcastSessionDataStoreFactory;
import org.eclipse.jetty.session.NullSessionCacheFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;

@Component(enabled = false, configurationPid = "com.enonic.xp.web.sessionstore")
public class HazelcastSessionStoreFactoryActivator
    extends AbstractSessionStoreFactoryActivator
{
    private final HazelcastInstance hazelcastInstance;

    private final WebSessionStoreConfigService webSessionStoreConfigService;

    @Activate
    public HazelcastSessionStoreFactoryActivator( final BundleContext bundleContext, @Reference final HazelcastInstance hazelcastInstance,
                                                  @Reference final WebSessionStoreConfigService webSessionStoreConfigService )
    {
        super( bundleContext );
        this.hazelcastInstance = hazelcastInstance;
        this.webSessionStoreConfigService = webSessionStoreConfigService;
    }

    @Activate
    public void activate()
    {
        final HazelcastSessionDataStoreFactory sessionDataStoreFactory = new HazelcastSessionDataStoreFactory();
        sessionDataStoreFactory.setHazelcastInstance( hazelcastInstance );
        sessionDataStoreFactory.setSavePeriodSec( webSessionStoreConfigService.getSavePeriodSeconds() );
        sessionDataStoreFactory.setGracePeriodSec( webSessionStoreConfigService.getGracePeriodSeconds() );
        final NullSessionCacheFactory sessionCacheFactory = new NullSessionCacheFactory();
        sessionCacheFactory.setSaveOnCreate( webSessionStoreConfigService.isSaveOnCreate() );
        sessionCacheFactory.setFlushOnResponseCommit( webSessionStoreConfigService.isFlushOnResponseCommit() );

        registerServices( sessionDataStoreFactory, sessionCacheFactory );
    }

    @Deactivate
    public void deactivate()
    {
        unregisterServices();
    }
}
