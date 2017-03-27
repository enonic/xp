package com.enonic.xp.region;


import com.google.common.annotations.Beta;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

@Beta
public final class PartDescriptor
    extends ComponentDescriptor
{
    private PartDescriptor( final Builder builder )
    {
        super( builder );
    }

    @Override
    public ResourceKey getComponentPath()
    {
        final DescriptorKey key = this.getKey();
        return ResourceKey.from( key.getApplicationKey(), "site/parts/" + key.getName() );
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/parts/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    public static PartDescriptor.Builder create()
    {
        return new Builder();
    }

    public static PartDescriptor.Builder copyOf( final PartDescriptor partDescriptor )
    {
        return new Builder( partDescriptor );
    }

    public final static class Builder
        extends BaseBuilder<Builder>
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
