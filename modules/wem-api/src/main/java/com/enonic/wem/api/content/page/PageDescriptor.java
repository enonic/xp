package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.region.RegionDescriptors;

public final class PageDescriptor
    extends Descriptor<PageDescriptorKey>
{
    private final RegionDescriptors regions;

    private PageDescriptor( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.regions, "regions cannot be null" );
        this.regions = builder.regions;
    }

    public RegionDescriptors getRegions()
    {
        return regions;
    }

    public static PageDescriptor.Builder newPageDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, PageDescriptorKey>
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

        public PageDescriptor build()
        {
            return new PageDescriptor( this );
        }
    }
}
