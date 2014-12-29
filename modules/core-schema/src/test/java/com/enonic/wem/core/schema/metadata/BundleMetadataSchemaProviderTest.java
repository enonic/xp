package com.enonic.wem.core.schema.metadata;

import org.junit.Test;
import org.osgi.framework.Bundle;

import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;
import com.enonic.wem.core.schema.AbstractBundleTest;

import static org.junit.Assert.*;

public class BundleMetadataSchemaProviderTest
    extends AbstractBundleTest
{
    @Test
    public void test_not_module()
        throws Exception
    {
        startBundles( newBundle( "not-module" ) );

        final Bundle bundle = findBundle( "not-module" );
        assertNotNull( bundle );

        final BundleMetadataSchemaProvider provider = BundleMetadataSchemaProvider.create( bundle );
        assertNull( provider );
    }

    @Test
    public void test_loaded_mixins()
        throws Exception
    {
        startBundles( newBundle( "module2" ) );

        final Bundle bundle = findBundle( "module2" );
        assertNotNull( bundle );

        final BundleMetadataSchemaProvider provider = BundleMetadataSchemaProvider.create( bundle );
        assertNotNull( provider );

        final MetadataSchemas values = provider.get();
        assertNotNull( values );
        assertEquals( 1, values.getSize() );

        final MetadataSchema schema = values.get( 0 );
        assertEquals( "simple", schema.getName().getLocalName() );
        assertNotNull( schema.getIcon() );
    }
}
