package com.enonic.xp.page;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public final class PageDescriptor
    extends ComponentDescriptor
{

    private final RegionDescriptors regions;

    private PageDescriptor( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.regions, "regions cannot be null" );
        this.regions = builder.regions;
    }

    @Override
    public ResourceKey getComponentPath()
    {
        final DescriptorKey key = this.getKey();
        return ResourceKey.from( key.getApplicationKey(), "site/pages/" + key.getName() );
    }

    public RegionDescriptors getRegions()
    {
        return regions;
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/pages/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    public static PageDescriptor.Builder create()
    {
        return new Builder();
    }

    public static PageDescriptor.Builder copyOf( final PageDescriptor pageDescriptor )
    {
        return new Builder( pageDescriptor );
    }

    public static class Builder
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
