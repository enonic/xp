package com.enonic.wem.core.schema.mixin;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.schema.AbstractBundleTest;

import static org.junit.Assert.*;

public class BundleMixinProviderTest
    extends AbstractBundleTest
{
    @Test
    public void test_not_module()
        throws Exception
    {
        startBundles( newBundle( "not-module" ) );

        final Bundle bundle = findBundle( "not-module" );
        assertNotNull( bundle );

        final BundleMixinProvider provider = BundleMixinProvider.create( bundle );
        assertNull( provider );
    }

    @Test
    public void test_loaded_mixins()
        throws Exception
    {
        startBundles( newBundle( "module1" ) );

        final Bundle bundle = findBundle( "module1" );
        assertNotNull( bundle );

        final BundleMixinProvider provider = BundleMixinProvider.create( bundle );
        assertNotNull( provider );

        final Mixins values = provider.get();
        assertNotNull( values );
        assertEquals( 2, values.getSize() );

        final Mixin mixin1 = values.get( 0 );
        assertEquals( "Address", mixin1.getDisplayName() );
        assertNotNull( mixin1.getIcon() );

        final Mixin mixin2 = values.get( 1 );
        assertEquals( "Full Name", mixin2.getDisplayName() );
        assertNull( mixin2.getIcon() );
    }
}

