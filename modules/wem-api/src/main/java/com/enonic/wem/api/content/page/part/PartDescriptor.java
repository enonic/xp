package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.resource.ResourceKey;

public class PartDescriptor
    extends Descriptor<PartDescriptorKey>
{
    private PartDescriptor( final Builder builder )
    {
        super( builder );
    }

    @Override
    public ResourceKey getComponentPath()
    {
        final PartDescriptorKey key = this.getKey();
        return ResourceKey.from( key.getModuleKey(), "part/" + key.getName().toString() );
    }

    public static PartDescriptor.Builder newPartDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, PartDescriptorKey>
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
