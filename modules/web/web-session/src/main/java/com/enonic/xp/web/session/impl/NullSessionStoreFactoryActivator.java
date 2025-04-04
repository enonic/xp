package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.session.DefaultSessionCache;
import org.eclipse.jetty.session.DefaultSessionCacheFactory;
import org.eclipse.jetty.session.NullSessionDataStoreFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(enabled = false)
public class NullSessionStoreFactoryActivator
    extends AbstractSessionStoreFactoryActivator
{
    @Activate
    public NullSessionStoreFactoryActivator( final BundleContext bundleContext )
    {
        super( bundleContext );
    }

    @Activate
    public void activate()
    {
        final NullSessionDataStoreFactory sessionDataStoreFactory = new NullSessionDataStoreFactory();
        final DefaultSessionCacheFactory sessionCacheFactory = new DefaultSessionCacheFactory();
        sessionCacheFactory.setEvictionPolicy( DefaultSessionCache.NEVER_EVICT );

        registerServices( sessionDataStoreFactory, sessionCacheFactory );
    }

    @Deactivate
    public void deactivate()
    {
        unregisterServices();
    }
}
