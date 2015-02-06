package com.enonic.wem.api.content.page.region;


import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.resource.ResourceKey;

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
        return ResourceKey.from( key.getModuleKey(), "cms/parts/" + key.getName().toString() );
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "cms/parts/" + key.getName().toString() + "/part.xml" );
    }

    public static PartDescriptor.Builder newPartDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, DescriptorKey>
    {
        private Builder()
        {
        }

        public PartDescriptor build()
        {
            return new PartDescriptor( this );
        }
    }
}
