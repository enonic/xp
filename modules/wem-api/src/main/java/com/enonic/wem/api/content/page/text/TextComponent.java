package com.enonic.wem.api.content.page.text;


import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public class TextComponent
    extends PageComponent<TextDescriptorKey>
    implements RegionPlaceableComponent
{
    private String text;

    public TextComponent( final Builder builder )
    {
        super( builder );
        this.text = builder.text;
    }

    @Override
    public Type getType()
    {
        return Type.TEXT;
    }

    public String getText()
    {
        return this.text;
    }

    public static Builder newTextComponent()
    {
        return new Builder();
    }

    public static class Builder
        extends PageComponent.Builder<TextDescriptorKey>
    {
        private String text;

        private Builder()
        {
        }

        public Builder text( final String value )
        {
            this.text = value;
            return this;
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = new ComponentName( value );
            return this;
        }

        public Builder descriptor( TextDescriptorKey value )
        {
            this.descrpitor = value;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public TextComponent build()
        {
            return new TextComponent( this );
        }
    }
}
