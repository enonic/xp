package com.enonic.xp.core.impl.app.descriptor;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.Descriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DescriptorServiceImplTest
    extends ApplicationTestSupport
{
    private DescriptorServiceImpl service;

    private MyDescriptorLoader loader;

    @Override
    protected void initialize()
    {
        final DescriptorFacetFactoryImpl facetFactory = new DescriptorFacetFactoryImpl( applicationService, resourceService );

        this.service = new DescriptorServiceImpl( facetFactory );

        this.loader = new MyDescriptorLoader();
        this.loader.resourceService = this.resourceService;

        addApplication( "myapp", "/myapp" );
        this.service.addLoader( this.loader );
    }

    @Test
    void testAddRemove()
    {
        final DescriptorKeys keys1 = this.service.findAll( MyDescriptor.class );
        assertThat( keys1 ).map( Objects::toString ).containsExactlyInAnyOrder( "myapp:type1", "myapp:type2" );

        this.service.removeLoader( this.loader );

        final DescriptorKeys keys2 = this.service.findAll( MyDescriptor.class );
        assertTrue( keys2.isEmpty() );
    }

    @Test
    void testFind()
    {
        final DescriptorKeys keys = this.service.find( MyDescriptor.class, ApplicationKeys.from( "myapp" ) );
        assertThat( keys ).map( Objects::toString ).containsExactlyInAnyOrder( "myapp:type1", "myapp:type2" );
    }

    @Test
    void testFindAll()
    {
        final DescriptorKeys keys = this.service.findAll( MyDescriptor.class );
        assertThat( keys ).map( Objects::toString ).containsExactlyInAnyOrder( "myapp:type1", "myapp:type2" );
    }

    @Test
    void testGet_single()
    {
        final MyDescriptor descriptor = this.service.get( MyDescriptor.class, DescriptorKey.from( "myapp:type1" ) );
        assertNotNull( descriptor );
    }

    @Test
    void testGet_application()
    {
        final Descriptors<MyDescriptor> descriptors = this.service.get( MyDescriptor.class, ApplicationKeys.from( "myapp" ) );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    void testGet_multiple()
    {
        final Descriptors<MyDescriptor> descriptors =
            this.service.get( MyDescriptor.class, DescriptorKeys.from( DescriptorKey.from( "myapp:type1" ) ) );
        assertEquals( 1, descriptors.getSize() );
    }

    @Test
    void testGetAll()
    {
        final Descriptors<MyDescriptor> descriptors = this.service.getAll( MyDescriptor.class );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    void testGetAll_loadFailure()
    {
        this.loader.loadException = true;

        final Descriptors<MyDescriptor> descriptors = this.service.getAll( MyDescriptor.class );
        assertEquals( 2, descriptors.getSize() );
    }
}
