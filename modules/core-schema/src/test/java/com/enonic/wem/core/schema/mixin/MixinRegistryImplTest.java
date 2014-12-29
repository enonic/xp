package com.enonic.wem.core.schema.mixin;

import org.junit.Ignore;

@Ignore
public class MixinRegistryImplTest
{
    /*
    private PojoServiceRegistry serviceRegistry;

    private MixinRegistryImpl registry;

    @Before
    public void setup()
        throws Exception
    {
        final Map<String, Object> config = Maps.newHashMap();
        this.serviceRegistry = new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry( config );
    }

    private void startBundles( final BundleDescriptor... bundles )
        throws Exception
    {
        this.serviceRegistry.startBundles( Lists.newArrayList( bundles ) );
    }

    private void activateRegistry()
    {
        final BundleContext bundleContext = this.serviceRegistry.getBundleContext();

        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( bundleContext );

        this.registry = new MixinRegistryImpl();
        this.registry.activate( componentContext );
    }

    @Test
    public void test_no_modules()
        throws Exception
    {
        activateRegistry();

        final Mixin mixin = this.registry.get( MixinName.from( "mymodule:address" ) );
        assertNull( mixin );

        final Collection<Mixin> result = this.registry.getAll();
        assertNotNull( result );
        assertEquals( 0, result.size() );
    }

    private BundleDescriptor newBundle( final String name )
    {
        final URL url = getClass().getResource( "/bundles/" + name + "/" );
        final URLClassLoader loader = new URLClassLoader( new URL[]{url}, null );

        final Map<String, String> headers = Maps.newHashMap();
        headers.put( Constants.BUNDLE_SYMBOLICNAME, name );
        headers.put( Constants.BUNDLE_VERSION, "1.0.0" );

        return new BundleDescriptor( loader, url, headers );
    }

    @Test
    public void test_installed_modules()
        throws Exception
    {
        startBundles( newBundle( "mymodule" ), newBundle( "othermodule" ) );
        activateRegistry();

        assertEquals( 3, this.registry.getAll().size() );

        final Mixin mixin = this.registry.get( MixinName.from( "mymodule:address" ) );
        assertNotNull( mixin );

        this.registry.deactivate();
        assertEquals( 0, this.registry.getAll().size() );
    }
    */
}
