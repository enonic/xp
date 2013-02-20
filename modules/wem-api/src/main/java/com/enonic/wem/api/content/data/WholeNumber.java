package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class WholeNumber
    extends Data
{
    public WholeNumber( final String name, final Long value )
    {
        super( Data.newData().name( name ).value( Value.newValue().type( DataTypes.WHOLE_NUMBER ).value( value ) ) );
    }

    public WholeNumber( final WholeNumberBuilder builder )
    {
        super( builder );
    }
}
