package com.enonic.xp.core.impl.app.descriptor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.SchemaService;

@Component
public final class DescriptorFacetFactoryImpl
    implements DescriptorFacetFactory
{
    private final SchemaService schemaService;

    private final ResourceService resourceService;

    @Activate
    public DescriptorFacetFactoryImpl( @Reference final SchemaService schemaService,
                                       @Reference final ResourceService resourceService )
    {
        this.schemaService = schemaService;
        this.resourceService = resourceService;
    }

    @Override
    public <T extends Descriptor> DescriptorFacet<T> create( final DescriptorLoader<T> loader )
    {
        final DescriptorFacetImpl<T> facet = new DescriptorFacetImpl<>( loader );
        facet.schemaService = this.schemaService;
        facet.resourceService = this.resourceService;
        return facet;
    }
}
