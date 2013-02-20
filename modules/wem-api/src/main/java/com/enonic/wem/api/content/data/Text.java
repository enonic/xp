package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class Text
    extends Data
{
    public Text( final String name, final String value )
    {
        super( Data.newData().name( name ).value( Value.newValue().type( DataTypes.TEXT ).value( value ) ) );
    }
}
