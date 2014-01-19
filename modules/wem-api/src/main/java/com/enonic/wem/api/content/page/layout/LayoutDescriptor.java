package com.enonic.wem.api.content.page.layout;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptors;

public class LayoutDescriptor
    extends Descriptor<LayoutDescriptorKey>
{
    private final RegionDescriptors regions;

    private LayoutDescriptor( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.regions, "regions cannot be null" );
        this.regions = builder.regions;
    }

    public RegionDescriptors getRegions()
    {
        return regions;
    }

    public static LayoutDescriptor.Builder newLayoutDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, LayoutDescriptorKey>
    {
        private RegionDescriptors regions;

        private Builder()
        {
        }

        public Builder regions( final RegionDescriptors value )
        {
            this.regions = value;
            return this;
        }

        public LayoutDescriptor build()
        {
            return new LayoutDescriptor( this );
        }
    }
}
