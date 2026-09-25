package com.enonic.xp.core.impl.app;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.node.NodeService;

@Component(immediate = true)
public class ApplicationFactoryServiceImpl
    implements ApplicationFactoryService
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationFactoryServiceImpl.class );

    private final BundleTracker<ApplicationAdaptor> bundleTracker;

    private final ApplicationFactory factory;

    @Activate
    public ApplicationFactoryServiceImpl( final BundleContext context, @Reference final NodeService nodeService )
    {
        this.factory = new ApplicationFactory( nodeService );

        this.bundleTracker =
            new BundleTracker<>( context, Bundle.INSTALLED + Bundle.RESOLVED + Bundle.STARTING + Bundle.STOPPING + Bundle.ACTIVE,
                                 new Customizer( factory ) );
    }

    @Activate
    public void activate()
    {
        bundleTracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        bundleTracker.close();
    }

    @Override
    public ApplicationAdaptor getApplication( final Bundle bundle )
    {
        return bundleTracker.getObject( bundle );
    }

    @Override
    public Optional<ApplicationAdaptor> findActiveApplication( final ApplicationKey applicationKey )
    {
        return findActiveEntry( applicationKey ).map( Map.Entry::getValue );
    }

    @Override
    public Optional<Bundle> findActiveBundle( final ApplicationKey applicationKey )
    {
        return findActiveEntry( applicationKey ).map( Map.Entry::getKey );
    }

    @Override
    public Optional<ApplicationUrlResolver> findResolver( final ApplicationKey applicationKey, final String source )
    {
        final Optional<Map.Entry<Bundle, ApplicationAdaptor>> adaptorEntry = findActiveEntry( applicationKey );

        if ( source == null )
        {
            return adaptorEntry.map( Map.Entry::getValue )
                .map( ApplicationAdaptor::getUrlResolver )
                .or( () -> findInactiveResolver( applicationKey ) );
        }

        return adaptorEntry.map( Map.Entry::getKey ).map( bundle -> factory.createUrlResolver( bundle, source ) );
    }

    // a stopped application still serves its persisted schema, the schema belongs to the installation, not to the running bundle
    private Optional<ApplicationUrlResolver> findInactiveResolver( final ApplicationKey applicationKey )
    {
        return entries( applicationKey ).findAny().map( Map.Entry::getKey ).map( factory::createInactiveUrlResolver );
    }

    private Optional<Map.Entry<Bundle, ApplicationAdaptor>> findActiveEntry( final ApplicationKey applicationKey )
    {
        return entries( applicationKey ).filter( bundleEntry -> bundleEntry.getKey().getState() == Bundle.ACTIVE ).findAny();
    }

    private Stream<Map.Entry<Bundle, ApplicationAdaptor>> entries( final ApplicationKey applicationKey )
    {
        return bundleTracker.getTracked()
            .entrySet()
            .stream()
            .filter( bundleEntry -> applicationKey.equals( ApplicationHelper.getApplicationKey( bundleEntry.getKey() ) ) );
    }

    private static class Customizer
        implements BundleTrackerCustomizer<ApplicationAdaptor>
    {
        private final ApplicationFactory factory;

        private Customizer( final ApplicationFactory factory )
        {
            this.factory = factory;
        }

        @Override
        public ApplicationImpl addingBundle( final Bundle bundle, final BundleEvent event )
        {
            if ( ApplicationBundleUtils.isApplication( bundle ) )
            {
                LOG.debug( "Creating new application instance from bundle {} {}", ApplicationBundleUtils.getApplicationName( bundle ),
                           bundle.getBundleId() );
                return factory.create( bundle );
            }
            else
            {
                return null;
            }
        }

        @Override
        public void modifiedBundle( final Bundle bundle, final BundleEvent event, final ApplicationAdaptor object )
        {
        }

        @Override
        public void removedBundle( final Bundle bundle, final BundleEvent event, final ApplicationAdaptor object )
        {
        }
    }
}