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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.ModuleState;
import com.enonic.wem.api.module.ModuleUpdatedEvent;
import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.LifecycleStage;
import com.enonic.wem.core.schema.ModuleSchemaProvider;

@Singleton
public final class ModuleLoader
    extends LifecycleBean
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
        super( LifecycleStage.L1 );
        this.bundles = Lists.newCopyOnWriteArrayList();
        this.context = context;
        this.moduleService = (ModuleServiceImpl) moduleService;
        this.xmlSerializer = new ModuleXmlBuilder();
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void doStart()
    {
        this.context.addBundleListener( this );
        for ( final Bundle bundle : this.context.getBundles() )
        {
            addBundle( bundle );
        }
    }

    @Override
    protected void doStop()
    {
        this.context.removeBundleListener( this );
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        switch ( event.getType() )
        {
            case BundleEvent.UNINSTALLED:
                removeBundle( bundle );
                break;

            case BundleEvent.INSTALLED:
            case BundleEvent.UPDATED:
                addBundle( bundle );
                break;

            case BundleEvent.STARTED:
                registerSchemas( bundle, moduleKey );
                break;
        }

        final ModuleState state = ModuleState.fromBundleState( bundle );
        this.eventPublisher.publish( new ModuleUpdatedEvent( moduleKey, state ) );
    }

    private void registerSchemas( final Bundle bundle, final ModuleKey moduleKey )
    {
        final Module module = findModule( moduleKey );
        if ( module != null )
        {
            final ModuleSchemaProvider moduleSchemaProvider = new ModuleSchemaProvider( module );
            // TODO register only if schemas found in module
            final BundleContext bundleContext = bundle.getBundleContext();
            bundleContext.registerService( SchemaProvider.class.getName(), moduleSchemaProvider, null );
        }
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
        this.xmlSerializer.toModule( moduleXml, moduleBuilder );

        final Dictionary<String, String> headers = bundle.getHeaders();
        final String bundleDisplayName = headers.get( Constants.BUNDLE_NAME );
        final String name = bundle.getSymbolicName();
        final String displayName = bundleDisplayName != null ? bundleDisplayName : name;

        moduleBuilder.moduleKey( ModuleKey.from( bundle ) );
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

    private Module findModule( final ModuleKey moduleKey )
    {
        try
        {
            return this.moduleService.getModule( moduleKey );
        }
        catch ( ModuleNotFoundException e )
        {
            return null;
        }
    }
}
