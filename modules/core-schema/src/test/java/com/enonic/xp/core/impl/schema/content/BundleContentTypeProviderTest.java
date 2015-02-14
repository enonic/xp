package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.core.impl.schema.AbstractBundleTest;

import static org.junit.Assert.*;

public class BundleContentTypeProviderTest
    extends AbstractBundleTest
{
    @Test
    public void test_not_module()
        throws Exception
    {
        startBundles( newBundle( "not-module" ) );

        final Bundle bundle = findBundle( "not-module" );
        assertNotNull( bundle );

        final BundleContentTypeProvider provider = BundleContentTypeProvider.create( bundle );
        assertNull( provider );
    }

    @Test
    public void test_loaded_mixins()
        throws Exception
    {
        startBundles( newBundle( "module1" ) );

        final Bundle bundle = findBundle( "module1" );
        assertNotNull( bundle );

        final BundleContentTypeProvider provider = BundleContentTypeProvider.create( bundle );
        assertNotNull( provider );

        final ContentTypes values = provider.get();
        assertNotNull( values );
        assertEquals( 1, values.getSize() );

        final ContentType type = values.get( 0 );
        assertEquals( "tag", type.getName().getLocalName() );
        assertNotNull( type.getIcon() );
    }
}
