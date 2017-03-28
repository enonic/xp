package com.enonic.xp.core.impl.app.descriptor;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.descriptor.DescriptorKey;

import static org.junit.Assert.*;

public class DescriptorServiceImplTest
    extends ApplicationTestSupport
{
    private DescriptorServiceImpl service;

    private MyDescriptorLoader loader;

    @Override
    protected void initialize()
        throws Exception
    {
        final DescriptorFacetFactoryImpl facetFactory = new DescriptorFacetFactoryImpl();
        facetFactory.setApplicationService( this.applicationService );
        facetFactory.setResourceService( this.resourceService );

        this.service = new DescriptorServiceImpl();
        this.service.setFacetFactory( facetFactory );

        this.loader = new MyDescriptorLoader();
        this.loader.resourceService = this.resourceService;

        addApplication( "myapp", "/myapp" );
        this.service.addLoader( this.loader );
    }

    @Test
    public void testAddRemove()
    {
        final DescriptorKeys keys1 = this.service.findAll( MyDescriptor.class );
        assertEquals( "[myapp:type2, myapp:type1]", keys1.toString() );

        this.service.removeLoader( this.loader );

        final DescriptorKeys keys2 = this.service.findAll( MyDescriptor.class );
        assertEquals( "[]", keys2.toString() );
    }

    @Test
    public void testFind()
    {
        final DescriptorKeys keys = this.service.find( MyDescriptor.class, ApplicationKeys.from( "myapp" ) );
        assertEquals( "[myapp:type2, myapp:type1]", keys.toString() );
    }

    @Test
    public void testFindAll()
    {
        final DescriptorKeys keys = this.service.findAll( MyDescriptor.class );
        assertEquals( "[myapp:type2, myapp:type1]", keys.toString() );
    }

    @Test
    public void testGet_single()
    {
        final MyDescriptor descriptor = this.service.get( MyDescriptor.class, DescriptorKey.from( "myapp:type1" ) );
        assertNotNull( descriptor );
    }

    @Test
    public void testGet_application()
    {
        final Descriptors<MyDescriptor> descriptors = this.service.get( MyDescriptor.class, ApplicationKeys.from( "myapp" ) );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    public void testGet_multiple()
    {
        final Descriptors<MyDescriptor> descriptors =
            this.service.get( MyDescriptor.class, DescriptorKeys.from( DescriptorKey.from( "myapp:type1" ) ) );
        assertEquals( 1, descriptors.getSize() );
    }

    @Test
    public void testGetAll()
    {
        final Descriptors<MyDescriptor> descriptors = this.service.getAll( MyDescriptor.class );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    public void testGetAll_loadFailure()
    {
        this.loader.loadException = true;

        final Descriptors<MyDescriptor> descriptors = this.service.getAll( MyDescriptor.class );
        assertEquals( 2, descriptors.getSize() );
    }
}
