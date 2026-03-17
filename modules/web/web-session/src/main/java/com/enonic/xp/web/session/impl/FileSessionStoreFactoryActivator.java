package com.enonic.xp.web.session.impl;

import java.io.File;

import org.eclipse.jetty.session.DefaultSessionCacheFactory;
import org.eclipse.jetty.session.FileSessionDataStoreFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(enabled = false, configurationPid = "com.enonic.xp.web.sessionstore")
public class FileSessionStoreFactoryActivator
    extends AbstractSessionStoreFactoryActivator
{
    private final WebSessionStoreConfigService webSessionStoreConfigService;

    @Activate
    public FileSessionStoreFactoryActivator( final BundleContext bundleContext,
                                             @Reference final WebSessionStoreConfigService webSessionStoreConfigService )
    {
        super( bundleContext );
        this.webSessionStoreConfigService = webSessionStoreConfigService;
    }

    @Activate
    public void activate()
    {
        final FileSessionDataStoreFactory sessionDataStoreFactory = new FileSessionDataStoreFactory();
        sessionDataStoreFactory.setStoreDir( new File( webSessionStoreConfigService.getStoreDir() ) );
        sessionDataStoreFactory.setGracePeriodSec( webSessionStoreConfigService.getGracePeriodSeconds() );
        sessionDataStoreFactory.setSavePeriodSec( webSessionStoreConfigService.getSavePeriodSeconds() );

        final DefaultSessionCacheFactory sessionCacheFactory = new DefaultSessionCacheFactory();
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
