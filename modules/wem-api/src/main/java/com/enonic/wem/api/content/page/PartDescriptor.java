package com.enonic.wem.api.content.page;


public class PartDescriptor
    extends BaseDescriptor
{
    private PartDescriptor( final Builder builder )
    {
        super( builder.name, builder.displayName, builder.controllerResource, builder.config );
    }

    public static PartDescriptor.Builder newPartDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder>
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
