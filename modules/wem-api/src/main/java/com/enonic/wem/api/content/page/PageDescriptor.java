package com.enonic.wem.api.content.page;


public final class PageDescriptor
    extends BaseDescriptor
{
    private PageDescriptor( final Builder builder )
    {
        super( builder.name, builder.displayName, builder.controllerResource, builder.config );
    }

    public static PageDescriptor.Builder newPageDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder>
    {
        private Builder()
        {
        }

        public PageDescriptor build()
        {
            return new PageDescriptor( this );
        }
    }
}
