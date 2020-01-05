package com.enonic.xp.web.session.impl.ignite;

import javax.cache.Cache;

import org.apache.ignite.Ignite;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;

import com.enonic.xp.web.session.impl.AbstractSessionDataStoreFactoryActivator;
import com.enonic.xp.web.session.impl.WebSessionConfig;

public class IgniteSessionDataStoreFactory
    extends org.eclipse.jetty.server.session.AbstractSessionDataStoreFactory
{
    private final Cache<String, IgniteSessionData> igniteCache;

    public IgniteSessionDataStoreFactory( final Ignite ignite, final WebSessionConfig config )
    {
        this.igniteCache = ignite.getOrCreateCache(
            IgniteCacheConfigFactory.create( AbstractSessionDataStoreFactoryActivator.WEB_SESSION_CACHE, config ) );
    }

    @Override
    public SessionDataStore getSessionDataStore( final SessionHandler handler )
    {
        final IgniteSessionDataStore isds = new IgniteSessionDataStore( igniteCache );
        isds.setSavePeriodSec( getSavePeriodSec() );
        return isds;
    }
}
