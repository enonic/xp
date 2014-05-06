package com.enonic.wem.api.content.page.text;


import com.enonic.wem.api.content.page.AbstractPageComponent;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.data.RootDataSet;

public class TextComponent
    extends AbstractPageComponent
{
    private static TextComponentType type = new TextComponentType();

    private String text;

    protected TextComponent( final Builder builder )
    {
        super( builder );
        this.text = builder.text;
    }

    public static Builder newTextComponent()
    {
        return new Builder();
    }

    public PageComponentType getType()
    {
        return TextComponent.type;
    }

    public String getText()
    {
        return text;
    }

    public static class Builder
        extends AbstractPageComponent.Builder

    {
        private String text;

        private Builder()
        {
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

        public Builder text( String value )
        {
            this.text = value;
            return this;
        }

        public TextComponent build()
        {
            return new TextComponent( this );
        }
    }
}
