package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class DecimalNumber
    extends Data
{
    public DecimalNumber( final String name, final Double value )
    {
        super( Data.newData().name( name ).value( Value.newValue().type( DataTypes.DECIMAL_NUMBER ).value( value ) ) );
    }

    DecimalNumber( final DecimalNumberBuilder decimalNumberBuilder )
    {
        super( decimalNumberBuilder );
    }
}
