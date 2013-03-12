package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class WholeNumber
    extends Data
{
    public WholeNumber( final String name, final Long value )
    {
        super( newWholeNumber().name( name ).value( value ) );
    }

    public WholeNumber( final WholeNumberBuilder builder )
    {
        super( builder );
    }

    public static WholeNumberBuilder newWholeNumber()
    {
        return new WholeNumberBuilder();
    }

    public static class WholeNumberBuilder
        extends BaseBuilder<WholeNumberBuilder>
    {
        public WholeNumberBuilder()
        {
            setType( DataTypes.WHOLE_NUMBER );
        }

        public WholeNumberBuilder value( final Long value )
        {
            setValue( value );
            return this;
        }

        @Override
        public Data build()
        {
            return new WholeNumber( this );
        }
    }
}
