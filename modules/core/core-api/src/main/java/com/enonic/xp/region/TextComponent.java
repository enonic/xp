package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.annotations.Beta;

@Beta
public class TextComponent
    extends Component
{
    private static final ComponentName NAME = ComponentName.from( "Text" );

    private String text;

    protected TextComponent( final Builder builder )
    {
        super( builder );
        this.text = builder.text != null ? builder.text : "";
    }

    public static <T extends Builder<T>> Builder<T> create()
    {
        return new Builder();
    }

    public static <T extends Builder<T>> Builder<T> create( final TextComponent source )
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

    @Override
    public ComponentName getName()
    {
        return NAME;
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

    public static class Builder<T extends Builder<T>>
        extends Component.Builder<T>

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

        public T text( String value )
        {
            this.text = value;
            return (T) this;
        }

        public TextComponent build()
        {
            return new TextComponent( this );
        }
    }
}
