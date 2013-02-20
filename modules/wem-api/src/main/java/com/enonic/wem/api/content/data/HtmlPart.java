package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class HtmlPart
    extends Data
{
    public HtmlPart( final String name, final String value )
    {
        super( Data.newData().name( name ).value( Value.newValue().type( DataTypes.HTML_PART ).value( value ) ) );
    }

    public HtmlPart( final HtmlPartBuilder builder )
    {
        super( builder );
    }
}
