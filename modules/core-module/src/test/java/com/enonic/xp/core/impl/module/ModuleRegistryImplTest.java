package com.enonic.xp.core.impl.module;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.kalpatec.pojosr.framework.PojoServiceRegistryFactoryImpl;
import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;

import com.enonic.xp.event.Event;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleEventType;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleUpdatedEvent;

import static org.junit.Assert.*;

public class ModuleRegistryImplTest
{
    private PojoServiceRegistry serviceRegistry;

    private ModuleRegistryImpl registry;

    private List<Event> events;

    @Before
    public void setup()
        throws Exception
    {
        final Map<String, Object> config = Maps.newHashMap();
        this.serviceRegistry = new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry( config );
        this.events = Lists.newArrayList();
    }

    private void startBundles( final BundleDescriptor... bundles )
        throws Exception
    {
        this.serviceRegistry.startBundles( Lists.newArrayList( bundles ) );
    }

    private void startRegistry()
    {
        final BundleContext bundleContext = this.serviceRegistry.getBundleContext();

        this.registry = new ModuleRegistryImpl();
        this.registry.setEventPublisher( this.events::add );
        this.registry.setBundleContext( bundleContext );

        this.registry.initialize();
    }

    @Test
    public void testStart_noModules()
        throws Exception
    {
        startRegistry();

        final Module module = this.registry.get( ModuleKey.from( "bundle1" ) );
        assertNull( module );

        final Collection<Module> result = this.registry.getAll();
        assertNotNull( result );
        assertEquals( 0, result.size() );
        assertEquals( 0, this.events.size() );
    }

    private void assertEvent( final int index, final ModuleEventType type, final ModuleKey key )
    {
        final ModuleUpdatedEvent event = (ModuleUpdatedEvent) this.events.get( index );
        assertEquals( type, event.getEventType() );
        assertEquals( key, event.getModuleKey() );
    }

    @Test
    public void testModuleInstalled()
        throws Exception
    {
        startBundles( newBundle( "bundle1", "Bundle 1" ), newBundle( "bundle2", "Bundle 2" ), newBundle( "bundle3", "Bundle 3" ) );
        startRegistry();

        assertEquals( 2, this.registry.getAll().size() );
        assertEquals( 2, this.events.size() );
        assertEvent( 0, ModuleEventType.STARTED, ModuleKey.from( "bundle1" ) );
        assertEvent( 1, ModuleEventType.STARTED, ModuleKey.from( "bundle3" ) );

        final Module module1 = this.registry.get( ModuleKey.from( "bundle1" ) );
        assertNotNull( module1 );
        assertEquals( "Bundle 1", module1.getDisplayName() );

        final Module module2 = this.registry.get( ModuleKey.from( "bundle3" ) );
        assertNotNull( module2 );
        assertEquals( "Bundle 3", module2.getDisplayName() );
    }

    @Test
    public void testModuleLifecycle()
        throws Exception
    {
        startBundles( newBundle( "bundle1", "Bundle 1" ) );
        startRegistry();

        final Module module = this.registry.get( ModuleKey.from( "bundle1" ) );
        assertNotNull( module );
        assertEquals( 1, this.registry.getAll().size() );
        assertEquals( 1, this.events.size() );
        assertEvent( 0, ModuleEventType.STARTED, ModuleKey.from( "bundle1" ) );

        this.registry.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, module.getBundle() ) );
        assertEquals( 0, this.registry.getAll().size() );
        assertNull( this.registry.get( ModuleKey.from( "bundle1" ) ) );
        assertEquals( 2, this.events.size() );
        assertEvent( 1, ModuleEventType.UNINSTALLED, ModuleKey.from( "bundle1" ) );

        this.registry.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, module.getBundle() ) );
        assertEquals( 1, this.registry.getAll().size() );
        assertNotNull( this.registry.get( ModuleKey.from( "bundle1" ) ) );
        assertEquals( 3, this.events.size() );
        assertEvent( 2, ModuleEventType.INSTALLED, ModuleKey.from( "bundle1" ) );
    }

    private BundleDescriptor newBundle( final String name, final String displayName )
    {
        final URL url = getClass().getResource( "/bundles/" + name + "/" );
        final URLClassLoader loader = new URLClassLoader( new URL[]{url}, null );

        final Map<String, String> headers = Maps.newHashMap();
        headers.put( Constants.BUNDLE_SYMBOLICNAME, name );
        headers.put( Constants.BUNDLE_VERSION, "1.0.0" );
        headers.put( Constants.BUNDLE_NAME, displayName );

        return new BundleDescriptor( loader, url, headers );
    }
}
