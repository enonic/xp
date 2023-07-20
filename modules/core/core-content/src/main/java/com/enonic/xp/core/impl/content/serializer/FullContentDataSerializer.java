package com.enonic.xp.core.impl.content.serializer;

import com.google.common.base.Preconditions;

import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;

public class FullContentDataSerializer extends ContentDataSerializer
{
    private FullContentDataSerializer( final Builder builder )
    {
        super( FullPageDataSerializer.create()
                   .pageDescriptorService( builder.pageDescriptorService )
                   .layoutDescriptorService( builder.layoutDescriptorService )
                   .build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PageDescriptorService pageDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        public Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        public Builder layoutDescriptorService( final LayoutDescriptorService value )
        {
            this.layoutDescriptorService = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( pageDescriptorService );
            Preconditions.checkNotNull( layoutDescriptorService );
        }

        public FullContentDataSerializer build()
        {
            validate();
            return new FullContentDataSerializer( this );
        }
    }

}
