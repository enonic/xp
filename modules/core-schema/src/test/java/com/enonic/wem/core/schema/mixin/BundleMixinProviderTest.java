package com.enonic.wem.core.schema.mixin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

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

        final List<Mixin> list = sort( values );

        final Mixin mixin1 = list.get( 0 );
        assertEquals( "Address", mixin1.getDisplayName() );
        assertNotNull( mixin1.getIcon() );

        final Mixin mixin2 = list.get( 1 );
        assertEquals( "Full Name", mixin2.getDisplayName() );
        assertNull( mixin2.getIcon() );
    }

    private List<Mixin> sort( final Mixins value )
    {
        final List<Mixin> list = Lists.newArrayList( value );
        Collections.sort( list, new Comparator<Mixin>()
        {
            @Override
            public int compare( final Mixin o1, final Mixin o2 )
            {
                return o1.getName().toString().compareTo( o2.getName().toString() );
            }
        } );
        return list;
    }
}
