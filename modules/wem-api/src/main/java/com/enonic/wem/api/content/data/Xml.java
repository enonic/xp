package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class Xml
    extends Data
{
    public Xml( final String name, final String value )
    {
        super( Data.newData().type( DataTypes.XML ).name( name ).value( value ) );
    }

    public Xml( final XmlBuilder builder )
    {
        super( builder );
    }
}
