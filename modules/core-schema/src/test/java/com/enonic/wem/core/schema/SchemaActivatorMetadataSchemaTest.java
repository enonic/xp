package com.enonic.wem.core.schema;

import java.util.List;

import com.enonic.wem.api.schema.metadata.MetadataSchemaProvider;

import static org.junit.Assert.*;

public class SchemaActivatorMetadataSchemaTest
    extends AbstractSchemaActivatorTest
{
    @Override
    protected void validateProviders()
        throws Exception
    {
        final List<MetadataSchemaProvider> list1 = getServices( null, MetadataSchemaProvider.class );
        assertEquals( 1, list1.size() );

        final List<MetadataSchemaProvider> list2 = getServices( "module1", MetadataSchemaProvider.class );
        assertEquals( 0, list2.size() );

        final List<MetadataSchemaProvider> list3 = getServices( "module2", MetadataSchemaProvider.class );
        assertEquals( 1, list3.size() );

        final List<MetadataSchemaProvider> list4 = getServices( "not-module", MetadataSchemaProvider.class );
        assertEquals( 0, list4.size() );
    }

    @Override
    protected void validateNoProviders()
        throws Exception
    {
        final List<MetadataSchemaProvider> list = getServices( null, MetadataSchemaProvider.class );
        assertEquals( 0, list.size() );
    }
}
