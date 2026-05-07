package com.enonic.xp.page;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.ResourceKey;

import static java.util.Objects.requireNonNull;


public final class PageDescriptor
    extends ComponentDescriptor
{

    private final RegionDescriptors regions;

    private PageDescriptor( final Builder builder )
    {
        super( builder );
        this.regions = requireNonNull( builder.regions, "regions cannot be null" );
    }

    @Override
    public ResourceKey getComponentPath()
    {
        final DescriptorKey key = this.getKey();
        return ResourceKey.from( key.getApplicationKey(), "cms/pages/" + key.getName() );
    }

    public RegionDescriptors getRegions()
    {
        return regions;
    }

    public static PageDescriptor.Builder create()
    {
        return new Builder();
    }

    public static PageDescriptor.Builder copyOf( final PageDescriptor pageDescriptor )
    {
        return new Builder( pageDescriptor );
    }

    public static final class Builder
        extends BaseBuilder<Builder>
    {
        private RegionDescriptors regions;

        private Builder( final PageDescriptor pageDescriptor )
        {
            super( pageDescriptor );
            this.regions = pageDescriptor.getRegions();
        }

        private Builder()
        {
        }

        public Builder regions( final RegionDescriptors value )
        {
            this.regions = value;
            return this;
        }

        public PageDescriptor build()
        {
            return new PageDescriptor( this );
        }
    }
}
