package com.enonic.xp.region;


import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class TextComponent
    extends Component
{
    private String text;

    protected TextComponent( final Builder builder )
    {
        super( builder );
        this.text = builder.text != null ? builder.text : "";
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final TextComponent source )
    {
        return new Builder( source );
    }

    @Override
    public TextComponent copy()
    {
        return create( this ).build();
    }

    @Override
    public ComponentType getType()
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

        return text != null ? text.equals( that.text ) : that.text == null;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), text );
    }

    public static class Builder
        extends Component.Builder

    {
        private String text;

        Builder()
        {
            // Default
        }

        private Builder( final TextComponent source )
        {
            super( source );
            text = source.text;
        }

        @Override
        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value != null ? new ComponentName( value ) : null;
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
