package com.enonic.xp.core.impl.app.descriptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DescriptorFacetFactoryImplTest
{
    private ResourceService resourceService;

    private ApplicationService applicationService;

    private MyDescriptorLoader descriptorLoader;

    private DescriptorFacetFactoryImpl facetFactory;

    @BeforeEach
    void setup()
    {
        this.resourceService = Mockito.mock( ResourceService.class );
        this.applicationService = Mockito.mock( ApplicationService.class );

        this.facetFactory = new DescriptorFacetFactoryImpl( this.applicationService, this.resourceService );

        this.descriptorLoader = new MyDescriptorLoader();
    }

    @Test
    void testCreate()
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
