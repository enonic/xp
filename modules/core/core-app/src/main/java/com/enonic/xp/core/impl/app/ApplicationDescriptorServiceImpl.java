package com.enonic.xp.core.impl.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.ApplicationBundleUtils;

@Component(immediate = true)
public class ApplicationDescriptorServiceImpl
    implements ApplicationDescriptorService, SynchronousBundleListener
{

    private static final Logger LOG = LoggerFactory.getLogger( ApplicationDescriptorServiceImpl.class );

    private final Map<ApplicationKey, ApplicationDescriptor> appDescriptorMap;

    public ApplicationDescriptorServiceImpl()
    {
        this.appDescriptorMap = new ConcurrentHashMap<>();
    }

    @Override
    public ApplicationDescriptor get( final ApplicationKey key )
    {
        return this.appDescriptorMap.get( key );
    }

    @Activate
    public void start( final ComponentContext context )
    {
        context.getBundleContext().addBundleListener( this );
        for ( final Bundle bundle : context.getBundleContext().getBundles() )
        {
            if ( !hasAppDescriptor( bundle ) )
            {
                continue;
            }

            addBundle( bundle );
        }
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();

        // we cannot check if the bundle is an app when it is uninstalled
        if ( event.getType() == BundleEvent.UNINSTALLED )
        {
            removeBundle( bundle );
            return;
        }

        if ( !hasAppDescriptor( bundle ) )
        {
            return;
        }

        switch ( event.getType() )
        {
            case BundleEvent.INSTALLED:
            case BundleEvent.UPDATED:
                addBundle( bundle );
                break;
            default:
                break;
        }
    }

    private boolean hasAppDescriptor( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && ApplicationDescriptorBuilder.hasAppDescriptor( bundle );
    }

    private void addBundle( final Bundle bundle )
    {
        try
        {
            registerApplicationDescriptor( bundle );
        }
        catch ( final Exception t )
        {
            LOG.warn( "Unable to load application descriptor for {}", ApplicationBundleUtils.getApplicationName( bundle ), t );
        }
    }

    private void registerApplicationDescriptor( final Bundle bundle )
    {
        final ApplicationDescriptorBuilder builder = new ApplicationDescriptorBuilder();
        builder.bundle( bundle );

        final ApplicationDescriptor applicationDescriptor = builder.build();
        registerApplicationDescriptor( ApplicationHelper.getApplicationKey( bundle ), applicationDescriptor );
    }

    private void registerApplicationDescriptor( final ApplicationKey applicationKey, final ApplicationDescriptor applicationDescriptor )
    {
        this.appDescriptorMap.put( applicationKey, applicationDescriptor );
    }

    private void removeBundle( final Bundle bundle )
    {
        final ApplicationKey applicationKey = ApplicationHelper.getApplicationKey( bundle );
        unregisterApplicationDescriptor( applicationKey );
    }

    private void unregisterApplicationDescriptor( final ApplicationKey applicationKey )
    {
        this.appDescriptorMap.remove( applicationKey );
    }
}
