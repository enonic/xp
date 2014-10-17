package com.enonic.wem.core.module;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.SynchronousBundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.module.ModuleEventType;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.ModuleUpdatedEvent;
import com.enonic.wem.api.module.ModuleVersion;

public final class ModuleLoader
    implements SynchronousBundleListener
{
    private static final String MODULE_XML = "/module.xml";

    private final static Logger LOG = LoggerFactory.getLogger( ModuleLoader.class );

    private final List<Bundle> bundles;

    private final BundleContext context;

    private final ModuleServiceImpl moduleService;

    private final ModuleXmlBuilder xmlSerializer;

    private final EventPublisher eventPublisher;

    public ModuleLoader( final BundleContext context, final ModuleService moduleService, final EventPublisher eventPublisher )
    {
        this.bundles = Lists.newCopyOnWriteArrayList();
        this.context = context;
        this.moduleService = (ModuleServiceImpl) moduleService;
        this.xmlSerializer = new ModuleXmlBuilder();
        this.eventPublisher = eventPublisher;
    }

    public void start()
    {
        this.context.addBundleListener( this );
        for ( final Bundle bundle : this.context.getBundles() )
        {
            addBundle( bundle );

            if ( bundle.getState() == Bundle.ACTIVE )
            {
                publishModuleChangeEvent( new BundleEvent( BundleEvent.STARTED, bundle ) );
            }
        }
    }

    public void stop()
    {
        this.context.removeBundleListener( this );
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();
        switch ( event.getType() )
        {
            case BundleEvent.UNINSTALLED:
                removeBundle( bundle );
                break;

            case BundleEvent.INSTALLED:
            case BundleEvent.UPDATED:
                addBundle( bundle );
                break;
        }

        publishModuleChangeEvent( event );
    }

    private void publishModuleChangeEvent( final BundleEvent event )
    {
        final ModuleKey moduleKey = ModuleKey.from( event.getBundle() );
        final ModuleEventType state = ModuleEventType.fromBundleEvent( event );
        this.eventPublisher.publish( new ModuleUpdatedEvent( moduleKey, state ) );
    }

    private void addBundle( final Bundle bundle )
    {
        if ( bundle.getResource( MODULE_XML ) == null )
        {
            return;
        }

        try
        {
            installModule( bundle );
            this.bundles.add( bundle );
            LOG.info( "Added web resource bundle [" + bundle.toString() + "]" );
        }
        catch ( Throwable t )
        {
            LOG.warn( "Unable to load module " + bundle.getSymbolicName(), t );
        }
    }

    private void removeBundle( final Bundle bundle )
    {
        if ( this.bundles.remove( bundle ) )
        {

            final ModuleKey moduleKey = ModuleKey.from( bundle );
            this.moduleService.uninstallModule( moduleKey );
            LOG.info( "Removed web resource bundle [" + bundle.toString() + "]" );
        }
    }

    private void installModule( final Bundle bundle )
    {
        final URL moduleResource = bundle.getResource( MODULE_XML );
        final String moduleXml = parseModuleXml( moduleResource );
        final ModuleBuilder moduleBuilder = new ModuleBuilder();
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        this.xmlSerializer.toModule( moduleXml, moduleBuilder, moduleKey );

        final Dictionary<String, String> headers = bundle.getHeaders();
        final String bundleDisplayName = headers.get( Constants.BUNDLE_NAME );
        final String name = bundle.getSymbolicName();
        final String displayName = bundleDisplayName != null ? bundleDisplayName : name;

        moduleBuilder.moduleKey( ModuleKey.from( bundle ) );
        moduleBuilder.moduleVersion( ModuleVersion.from( bundle.getVersion().toString() ) );
        moduleBuilder.displayName( displayName );
        moduleBuilder.bundle( bundle );

        this.moduleService.installModule( moduleBuilder.build() );
    }

    private String parseModuleXml( final URL moduleResource )
    {
        try
        {
            return Resources.toString( moduleResource, Charsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Invalid module.xml file", e );
        }
    }
}
