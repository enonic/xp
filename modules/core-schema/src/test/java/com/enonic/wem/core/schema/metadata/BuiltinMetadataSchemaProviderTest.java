package com.enonic.wem.core.schema.metadata;

import org.junit.Test;

import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

import static org.junit.Assert.*;

public class BuiltinMetadataSchemaProviderTest
{
    @Test
    public void testBuiltin()
    {
        final MetadataSchemas schemas = new BuiltinMetadataSchemaProvider().get();
        assertEquals( 1, schemas.getSize() );

        assertSchema( schemas.get( 0 ), MetadataSchemaName.from( "system:menu-item" ), false );
    }

    private void assertSchema( final MetadataSchema schema, final MetadataSchemaName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), schema.getName().toString() );
        assertEquals( hasIcon, schema.getIcon() != null );
    }
}
