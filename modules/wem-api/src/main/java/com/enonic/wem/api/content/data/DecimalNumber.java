package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class DecimalNumber
    extends Data
{
    public DecimalNumber( final String name, final Double value )
    {
        super( newDecimalNumber().name( name ).value( value ) );
    }

    DecimalNumber( final DecimalNumberBuilder decimalNumberBuilder )
    {
        super( decimalNumberBuilder );
    }

    public static DecimalNumberBuilder newDecimalNumber()
    {
        return new DecimalNumberBuilder();
    }

    public static class DecimalNumberBuilder
        extends BaseBuilder<DecimalNumberBuilder>
    {
        public DecimalNumberBuilder()
        {
            setType( DataTypes.DECIMAL_NUMBER );
        }

        public DecimalNumberBuilder value( final Double value )
        {
            setValue( value );
            return this;
        }

        @Override
        public DecimalNumber build()
        {
            return new DecimalNumber( this );
        }
    }
}
