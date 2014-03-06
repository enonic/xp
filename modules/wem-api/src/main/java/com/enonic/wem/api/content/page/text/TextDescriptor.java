package com.enonic.wem.api.content.page.text;

import com.enonic.wem.api.content.page.Descriptor;

public class TextDescriptor
    extends Descriptor<TextDescriptorKey>
{
    private TextDescriptor( final Builder builder )
    {
        super( builder );
    }

    public static TextDescriptor.Builder newTextDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, TextDescriptorKey>
    {
        private Builder()
        {
        }

        public TextDescriptor build()
        {
            return new TextDescriptor( this );
        }
    }
}
