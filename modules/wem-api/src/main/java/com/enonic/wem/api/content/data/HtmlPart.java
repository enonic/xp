package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class HtmlPart
    extends Data
{
    public HtmlPart( final String name, final String value )
    {
        super( newHtmlPart().name( name ).value( value ) );
    }

    public HtmlPart( final HtmlPartBuilder builder )
    {
        super( builder );
    }

    public static HtmlPartBuilder newHtmlPart()
    {
        return new HtmlPartBuilder();
    }

    public static class HtmlPartBuilder
        extends BaseBuilder<HtmlPartBuilder>
    {
        public HtmlPartBuilder()
        {
            setType( DataTypes.HTML_PART );
        }

        public HtmlPartBuilder value( final String value )
        {
            setValue( value );
            return this;
        }

        @Override
        public Data build()
        {
            return new HtmlPart( this );
        }
    }
}
