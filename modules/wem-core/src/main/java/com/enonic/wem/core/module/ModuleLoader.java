package com.enonic.wem.core.module;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.ModuleState;
import com.enonic.wem.api.module.ModuleUpdatedEvent;
import com.enonic.wem.api.module.ModuleVersion;

@Singleton
public final class ModuleLoader
    implements BundleListener
{

    private static final String MODULE_XML = "/module.xml";

    private final static Logger LOG = LoggerFactory.getLogger( ModuleLoader.class );

    private final List<Bundle> bundles;

    private final BundleContext context;

    private final ModuleServiceImpl moduleService;

    private final ModuleXmlBuilder xmlSerializer;

    private final EventPublisher eventPublisher;

    @Inject
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

        final ModuleKey module = getModuleKey( bundle );
        final ModuleState state = ModuleState.fromBundleState( bundle );
        this.eventPublisher.publish( new ModuleUpdatedEvent( module, state ) );
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

            final ModuleKey moduleKey = getModuleKey( bundle );
            this.moduleService.uninstallModule( moduleKey );
            LOG.info( "Removed web resource bundle [" + bundle.toString() + "]" );
        }
    }

    private ModuleKey getModuleKey( final Bundle bundle )
    {
        final String name = bundle.getSymbolicName();
        final Version bundleVersion = bundle.getVersion();
        final ModuleVersion version = ModuleVersion.from( bundleVersion.toString() );
        return ModuleKey.from( ModuleName.from( name ), version );
    }

    private void installModule( final Bundle bundle )
    {
        final URL moduleResource = bundle.getResource( MODULE_XML );
        final String moduleXml = parseModuleXml( moduleResource );
        final ModuleBuilder moduleBuilder = new ModuleBuilder();
        this.xmlSerializer.toModule( moduleXml, moduleBuilder );

        final Dictionary<String, String> headers = bundle.getHeaders();
        final String bundleDisplayName = headers.get( Constants.BUNDLE_NAME );
        final String name = bundle.getSymbolicName();
        final String displayName = bundleDisplayName != null ? bundleDisplayName : name;

        moduleBuilder.moduleKey( getModuleKey( bundle ) );
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
