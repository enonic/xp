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

import com.enonic.xp.app.ApplicationBundleUtils;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.server.RunMode;

@Component(immediate = true)
public class ApplicationFactoryServiceImpl
    implements ApplicationFactoryService
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationFactoryServiceImpl.class );

    private final BundleTracker<ApplicationAdaptor> bundleTracker;

    private final NodeService nodeService;

    @Activate
    public ApplicationFactoryServiceImpl( final BundleContext context, @Reference final NodeService nodeService )
    {
        this.nodeService = nodeService;
        bundleTracker =
            new BundleTracker<>( context, Bundle.INSTALLED + Bundle.RESOLVED + Bundle.STARTING + Bundle.STOPPING + Bundle.ACTIVE,
                                 new Customizer( nodeService ) );
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
            .filter( bundleEntry -> applicationKey.equals( ApplicationKey.from( bundleEntry.getKey() ) ) )
            .filter( bundleEntry -> bundleEntry.getKey().getState() == Bundle.ACTIVE )
            .findAny()
            .map( Map.Entry::getValue ).or( () -> findVirtualApp(applicationKey) );
    }

    public Optional<ApplicationAdaptor> findVirtualApp( final ApplicationKey applicationKey )
    {
        PropertyTree request = new PropertyTree();
        final PropertySet likeExpression = request.addSet( "like" );
        likeExpression.addString( "field", "_path" );
        likeExpression.addString( "value", "/" + applicationKey );

        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByQueryResult nodes = this.nodeService.findByQuery(
                NodeQuery.create().query( QueryExpr.from( DslExpr.from( request ) ) ).withPath( true ).build() );
            if ( nodes.getTotalHits() != 0 )
            {
                return Optional.of( VirtualAppFactory.create( applicationKey, nodeService ) );
            }
            else
            {
                return Optional.empty();
            }
        } );
    }

    private static class Customizer
        implements BundleTrackerCustomizer<ApplicationAdaptor>
    {
        private final ApplicationFactory factory;

        public Customizer( final NodeService nodeService )
        {
            factory = new ApplicationFactory( RunMode.get(), nodeService );
        }

        @Override
        public ApplicationImpl addingBundle( final Bundle bundle, final BundleEvent event )
        {
            if ( ApplicationBundleUtils.isApplication( bundle ) )
            {
                LOG.debug( "Creating new application instance from bundle {} {}", bundle.getSymbolicName(), bundle.getBundleId() );
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
