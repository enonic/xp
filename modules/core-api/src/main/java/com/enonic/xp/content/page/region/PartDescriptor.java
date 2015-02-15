package com.enonic.xp.content.page.region;


import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

public class PartDescriptor
    extends Descriptor<DescriptorKey>
{
    private PartDescriptor( final Builder builder )
    {
        super( builder );
    }

    @Override
    public ResourceKey getComponentPath()
    {
        final DescriptorKey key = this.getKey();
        return ResourceKey.from( key.getModuleKey(), "cms/parts/" + key.getName() );
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "cms/parts/" + key.getName() + "/part.xml" );
    }

    public static PartDescriptor.Builder create()
    {
        return new Builder();
    }

    public static PartDescriptor.Builder copyOf( final PartDescriptor partDescriptor )
    {
        return new Builder(partDescriptor);
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, DescriptorKey>
    {
        private Builder()
        {
        }

        private Builder( final PartDescriptor partDescriptor )
        {
            super( partDescriptor );
        }

        public PartDescriptor build()
        {
            return new PartDescriptor( this );
        }
    }
}
