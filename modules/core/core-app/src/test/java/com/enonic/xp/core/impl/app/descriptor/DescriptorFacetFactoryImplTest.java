package com.enonic.xp.core.impl.app.descriptor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;

import static org.junit.Assert.*;

public class DescriptorFacetFactoryImplTest
{
    private ResourceService resourceService;

    private ApplicationService applicationService;

    private MyDescriptorLoader descriptorLoader;

    private DescriptorFacetFactoryImpl facetFactory;

    @Before
    public void setup()
    {
        this.resourceService = Mockito.mock( ResourceService.class );
        this.applicationService = Mockito.mock( ApplicationService.class );

        this.facetFactory = new DescriptorFacetFactoryImpl();
        this.facetFactory.setResourceService( this.resourceService );
        this.facetFactory.setApplicationService( this.applicationService );

        this.descriptorLoader = new MyDescriptorLoader();
    }

    @Test
    public void testCreate()
    {
        final DescriptorFacet<MyDescriptor> facet = this.facetFactory.create( this.descriptorLoader );
        assertNotNull( facet );
        assertTrue( facet instanceof DescriptorFacetImpl );

        final DescriptorFacetImpl<MyDescriptor> implFacet = (DescriptorFacetImpl<MyDescriptor>) facet;
        assertSame( this.resourceService, implFacet.resourceService );
        assertSame( this.applicationService, implFacet.applicationService );
        assertSame( this.descriptorLoader, implFacet.loader );
    }
}
