package com.enonic.xp.core.impl.app;

import java.util.Collection;

import org.junit.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class ApplicationRegistryImplTest
    extends ApplicationBundleTest
{
    private ApplicationRegistryImpl registry;

    @Override
    protected void initialize()
        throws Exception
    {
        this.registry = new ApplicationRegistryImpl();
        this.registry.start( this.bundleContext );
    }

    @Test
    public void testNoApps()
        throws Exception
    {
        startBundles();

        final Application application = this.registry.get( ApplicationKey.from( "bundle1" ) );
        assertNull( application );

        final Collection<Application> result = this.registry.getAll();
        assertNotNull( result );
        assertEquals( 0, result.size() );
    }

    @Test
    public void testApps()
        throws Exception
    {
        startBundles( newBundle( "bundle1", "Bundle 1" ), newBundle( "bundle2", "Bundle 2" ), newBundle( "bundle3", "Bundle 3" ) );

        assertEquals( 2, this.registry.getAll().size() );

        final Application application1 = this.registry.get( ApplicationKey.from( "bundle1" ) );
        assertNotNull( application1 );
        assertEquals( "Bundle 1", application1.getDisplayName() );

        final Application application2 = this.registry.get( ApplicationKey.from( "bundle3" ) );
        assertNotNull( application2 );
        assertEquals( "Bundle 3", application2.getDisplayName() );
    }

    @Test
    public void testInvaldate()
        throws Exception
    {
        startBundles( newBundle( "bundle1", "Bundle 1" ) );

        assertNotNull( this.registry.get( ApplicationKey.from( "bundle1" ) ) );
        assertEquals( 1, this.registry.getAll().size() );

        this.registry.invalidate( ApplicationKey.from( "bundle1" ) );

        assertEquals( 1, this.registry.getAll().size() );
        assertNotNull( this.registry.get( ApplicationKey.from( "bundle1" ) ) );
    }
}
