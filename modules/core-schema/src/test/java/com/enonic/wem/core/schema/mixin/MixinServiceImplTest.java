package com.enonic.wem.core.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.ComponentContext;

import com.enonic.wem.api.schema.mixin.Mixins;

import static org.junit.Assert.*;

public class MixinServiceImplTest
{
    private MixinRegistryImpl registry;

    private MixinServiceImpl service;

    private BundleContext bundleContext;

    private ComponentContext componentContext;

    private BundleListener bundleListener;

    @Before
    public void setup()
    {
        this.bundleContext = Mockito.mock( BundleContext.class );
        this.componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( this.componentContext.getBundleContext() ).thenReturn( this.bundleContext );

        this.registry = new MixinRegistryImpl();

        this.service = new MixinServiceImpl();
        this.service.setRegistry( this.registry );
    }

    private void activate()
    {
        this.registry.activate( this.componentContext );

        final ArgumentCaptor<BundleListener> captor = ArgumentCaptor.forClass( BundleListener.class );
        Mockito.verify( this.bundleContext ).addBundleListener( captor.capture() );
        this.bundleListener = captor.getValue();
    }

    private void deactivate()
    {
        this.registry.deactivate();
        Mockito.verify( this.bundleContext ).removeBundleListener( this.bundleListener );
    }

    @Test
    public void testEmpty()
    {
        final Mixins result = this.service.getAll();
        assertNotNull( result );
        assertEquals( 0, result.getSize() );
    }

    @Test
    public void testLifecycle()
    {
        activate();
        deactivate();
    }

    @Test
    public void testInstallModule()
        throws Exception
    {
    }
}
