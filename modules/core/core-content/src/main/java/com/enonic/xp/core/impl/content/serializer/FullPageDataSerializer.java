package com.enonic.xp.core.impl.content.serializer;

import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.RegionDescriptors;

final class FullPageDataSerializer
    extends PageDataSerializer
{
    private final PageDescriptorService pageDescriptorService;

    private FullPageDataSerializer( final Builder builder )
    {
        this.pageDescriptorService = builder.pageDescriptorService;
        this.componentDataSerializerProvider = new ComponentDataSerializerProvider( builder.layoutDescriptorService );
    }

    protected PageRegions getPageRegions( final DescriptorKey descriptorKey, final List<PropertySet> componentsAsData )
    {
        final PageDescriptor pageDescriptor = pageDescriptorService.getByKey( descriptorKey );

        final RegionDescriptors regionDescriptors = pageDescriptor.getRegions();

        final PageRegions.Builder pageRegionsBuilder = PageRegions.create();

        regionDescriptors.forEach( regionDescriptor -> {
            pageRegionsBuilder.add( componentDataSerializerProvider.getRegionDataSerializer()
                                        .fromData( regionDescriptor, ComponentPath.DIVIDER, componentsAsData ) );
        } );

        return pageRegionsBuilder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PageDescriptorService pageDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        public Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        public Builder layoutDescriptorService( final LayoutDescriptorService value )
        {
            this.layoutDescriptorService = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( pageDescriptorService );
            Preconditions.checkNotNull( layoutDescriptorService );
        }

        public FullPageDataSerializer build()
        {
            validate();
            return new FullPageDataSerializer( this );
        }
    }
}
