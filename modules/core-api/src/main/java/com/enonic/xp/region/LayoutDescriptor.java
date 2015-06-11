package com.enonic.xp.region;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

@Beta
public class LayoutDescriptor
    extends Descriptor<DescriptorKey>
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

    @Override
    public ResourceKey getComponentPath()
    {
        final DescriptorKey key = this.getKey();
        return ResourceKey.from( key.getModuleKey(), "app/layouts/" + key.getName() );
    }

    public static LayoutDescriptor.Builder create()
    {
        return new Builder();
    }

    public static LayoutDescriptor.Builder copyOf( final LayoutDescriptor layoutDescriptor )
    {
        return new Builder( layoutDescriptor );
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "app/layouts/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, DescriptorKey>
    {
        private RegionDescriptors regions;

        private Builder()
        {
        }

        private Builder( final LayoutDescriptor layoutDescriptor )
        {
            super( layoutDescriptor );
            this.regions = layoutDescriptor.getRegions();
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
