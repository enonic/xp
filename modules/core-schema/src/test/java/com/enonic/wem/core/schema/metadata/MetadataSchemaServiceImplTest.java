package com.enonic.wem.core.schema.metadata;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaProvider;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

import static org.junit.Assert.*;

public class MetadataSchemaServiceImplTest
{
    private MetadataSchemaServiceImpl service;

    private MetadataSchemaProvider provider;

    private MetadataSchema schema1;

    private MetadataSchema schema2;

    @Before
    public void setup()
    {
        this.service = new MetadataSchemaServiceImpl();
        this.schema1 = createSchema( "mymodule:test" );
        this.schema2 = createSchema( "othermodule:test" );
        this.provider = () -> MetadataSchemas.from( this.schema1, this.schema2 );
    }

    @Test
    public void testEmpty()
    {
        final MetadataSchemas result = this.service.getAll();
        assertNotNull( result );
        assertEquals( 0, result.getSize() );
    }

    @Test
    public void testGetByName()
    {
        this.service.addProvider( this.provider );

        final MetadataSchema result1 = this.service.getByName( this.schema1.getName() );
        assertNotNull( result1 );

        this.service.removeProvider( this.provider );

        final MetadataSchema result2 = this.service.getByName( this.schema1.getName() );
        assertNull( result2 );
    }

    @Test
    public void testGetAll()
    {
        this.service.addProvider( this.provider );

        final MetadataSchemas result = this.service.getAll();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
        assertSame( this.schema1, result.get( 1 ) );
        assertSame( this.schema2, result.get( 0 ) );
    }

    @Test
    public void testGetByModule()
    {
        this.service.addProvider( this.provider );

        final MetadataSchemas result = this.service.getByModule( ModuleKey.from( "mymodule" ) );
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( this.schema1, result.get( 0 ) );
    }

    private MetadataSchema createSchema( final String name )
    {
        return MetadataSchema.newMetadataSchema().name( name ).build();
    }
}
