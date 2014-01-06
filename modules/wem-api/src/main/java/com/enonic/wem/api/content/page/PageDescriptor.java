package com.enonic.wem.api.content.page;


public final class PageDescriptor
    extends Descriptor<PageDescriptorKey>
{
    private PageDescriptor( final Builder builder )
    {
        super( builder );
    }

    public static PageDescriptor.Builder newPageDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, PageDescriptorKey>
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
