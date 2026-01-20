package com.enonic.xp.core.impl.app;

import java.util.Map;
import java.util.Optional;

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
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;

@Component(immediate = true, configurationPid = "com.enonic.xp.app")
public class ApplicationFactoryServiceImpl
    implements ApplicationFactoryService
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationFactoryServiceImpl.class );

    private final BundleTracker<ApplicationAdaptor> bundleTracker;

    private final NodeService nodeService;

    private final AppConfig appConfig;

    private final ApplicationFactory factory;

    @Activate
    public ApplicationFactoryServiceImpl( final BundleContext context, @Reference final NodeService nodeService, final AppConfig config )
    {
        this.nodeService = nodeService;
        this.appConfig = config;

        this.factory = new ApplicationFactory( nodeService, appConfig );

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
        return bundleTracker.getTracked()
            .entrySet()
            .stream()
            .filter( bundleEntry -> applicationKey.equals( ApplicationHelper.getApplicationKey( bundleEntry.getKey() ) ) )
            .filter( bundleEntry -> bundleEntry.getKey().getState() == Bundle.ACTIVE )
            .findAny()
            .map( Map.Entry::getValue )
            .or( () -> findVirtualApp( applicationKey ) );
    }

    @Override
    public Optional<ApplicationUrlResolver> findResolver( final ApplicationKey applicationKey, final String source )
    {
        final Optional<Map.Entry<Bundle, ApplicationAdaptor>> adaptorEntry = bundleTracker.getTracked()
            .entrySet()
            .stream()
            .filter( bundleEntry -> applicationKey.equals( ApplicationHelper.getApplicationKey( bundleEntry.getKey() ) ) )
            .filter( bundleEntry -> bundleEntry.getKey().getState() == Bundle.ACTIVE )
            .findAny();

        if ( source == null )
        {
            return adaptorEntry.map( Map.Entry::getValue )
                .or( () -> findVirtualApp( applicationKey ) )
                .map( ApplicationAdaptor::getUrlResolver );
        }

        return adaptorEntry.map( Map.Entry::getKey ).map( bundle -> factory.createUrlResolver( bundle, source ) );
    }

    private Optional<ApplicationAdaptor> findVirtualApp( final ApplicationKey applicationKey )
    {
        if ( !appConfig.virtual_enabled() )
        {
            return Optional.empty();
        }

        return VirtualAppContext.createContext().callWith( () -> {
            final NodePath appPath = new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.getName() ) );
            return this.nodeService.nodeExists( appPath )
                ? Optional.of( VirtualAppFactory.create( applicationKey, nodeService ) )
                : Optional.empty();
        } );
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
