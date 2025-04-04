package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.session.SessionCacheFactory;
import org.eclipse.jetty.session.SessionDataStoreFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

abstract class AbstractSessionStoreFactoryActivator
{
    private final BundleContext bundleContext;

    private ServiceRegistration<SessionDataStoreFactory> sessionDataStoreFactoryReg;

    private ServiceRegistration<SessionCacheFactory> sessionCacheReg;

    AbstractSessionStoreFactoryActivator( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public final void registerServices( final SessionDataStoreFactory sessionDataStoreFactory, SessionCacheFactory sessionCacheFactory )
    {
        sessionDataStoreFactoryReg = bundleContext.registerService( SessionDataStoreFactory.class, sessionDataStoreFactory, null );
        sessionCacheReg = bundleContext.registerService( SessionCacheFactory.class, sessionCacheFactory, null );
    }

    public final void unregisterServices()
    {
        if ( sessionDataStoreFactoryReg != null )
        {
            sessionDataStoreFactoryReg.unregister();
        }
        if ( sessionCacheReg != null )
        {
            sessionCacheReg.unregister();
        }
    }
}
