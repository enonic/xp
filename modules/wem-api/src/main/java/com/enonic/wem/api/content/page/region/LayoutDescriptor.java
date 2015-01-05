package com.enonic.wem.api.content.page.region;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.resource.ResourceKey;

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
        return ResourceKey.from( key.getModuleKey(), "layout/" + key.getName().toString() );
    }

    public static LayoutDescriptor.Builder newLayoutDescriptor()
    {
        return new Builder();
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "layout/" + key.getName().toString() + "/layout.xml" );
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, DescriptorKey>
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
