package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class Text
    extends Data
{
    public Text( final String name, final String value )
    {
        super( newText().name( name ).value( value ) );
    }

    public Text( final TextBuilder builder )
    {
        super( builder );
    }

    public static TextBuilder newText()
    {
        return new TextBuilder();
    }

    public static class TextBuilder
        extends BaseBuilder<TextBuilder>
    {
        public TextBuilder()
        {
            setType( DataTypes.TEXT );
        }

        public TextBuilder value( final String value )
        {
            setValue( value );
            return this;
        }

        @Override
        public Data build()
        {
            return new Text( this );
        }
    }
}
