package com.enonic.xp.region;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public final class PartDescriptor
    extends ComponentDescriptor
{
    private final Icon icon;

    private PartDescriptor( final Builder builder )
    {
        super( builder );
        this.icon = builder.icon;
    }

    @Override
    public ResourceKey getComponentPath()
    {
        final DescriptorKey key = this.getKey();
        return ResourceKey.from( key.getApplicationKey(), "site/parts/" + key.getName() );
    }

    public static ResourceKey toResourceKey( final DescriptorKey key, final String ext )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/parts/" + key.getName() + "/" + key.getName() + "." + ext );
    }

    public Icon getIcon()
    {
        return icon;
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
        private Icon icon;

        private Builder()
        {
        }

        private Builder( final PartDescriptor partDescriptor )
        {
            super( partDescriptor );
            this.icon = partDescriptor.icon;
        }

        public PartDescriptor.Builder icon( Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public PartDescriptor build()
        {
            return new PartDescriptor( this );
        }
    }
}
