package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.server.session.SessionCacheFactory;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class AbstractSessionDataStoreFactoryActivator
{
    public static final String WEB_SESSION_CACHE = "com.enonic.xp.webSessionCache";

    private final BundleContext bundleContext;

    private ServiceRegistration<SessionDataStoreFactory> sessionDataStoreFactoryReg;

    private ServiceRegistration<SessionCacheFactory> sessionCacheReg;

    protected AbstractSessionDataStoreFactoryActivator( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    protected final void registerServices( final SessionDataStoreFactory sessionDataStoreFactory, SessionCacheFactory sessionCacheFactory )
    {
        sessionDataStoreFactoryReg = bundleContext.registerService( SessionDataStoreFactory.class, sessionDataStoreFactory, null );
        sessionCacheReg = bundleContext.registerService( SessionCacheFactory.class, sessionCacheFactory, null );

    }

    public void unregisterServices()
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