package com.enonic.xp.core.impl.app.descriptor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.resource.ResourceService;

@Component
public final class DescriptorFacetFactoryImpl
    implements DescriptorFacetFactory
{
    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    @Activate
    public DescriptorFacetFactoryImpl( @Reference final ApplicationService applicationService,
                                       @Reference final ResourceService resourceService )
    {
        this.applicationService = applicationService;
        this.resourceService = resourceService;
    }

    @Override
    public <T extends Descriptor> DescriptorFacet<T> create( final DescriptorLoader<T> loader )
    {
        final DescriptorFacetImpl<T> facet = new DescriptorFacetImpl<>( loader );
        facet.applicationService = this.applicationService;
        facet.resourceService = this.resourceService;
        return facet;
    }
}
