package com.enonic.wem.api.content.page.text;


import java.util.Objects;

import com.enonic.wem.api.content.page.AbstractPageComponent;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponentType;

public class TextComponent
    extends AbstractPageComponent
{
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
        return TextComponentType.INSTANCE;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final TextComponent that = (TextComponent) o;

        if ( text != null ? !text.equals( that.text ) : that.text != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), text );
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
