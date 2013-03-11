package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;

import com.enonic.wem.api.content.data.type.DataTypes;

public final class Date
    extends Data
{
    public Date( final String name, final DateMidnight value )
    {
        super( newDate().name( name ).value( value ) );
    }

    Date( final DateBuilder dateBuilder )
    {
        super( dateBuilder );
    }

    public static DateBuilder newDate()
    {
        return new DateBuilder();
    }

    public static class DateBuilder
        extends BaseBuilder<DateBuilder>
    {
        public DateBuilder()
        {
            setType( DataTypes.DATE_MIDNIGHT );
        }

        public DateBuilder value( final DateMidnight value )
        {
            setValue( value );
            return this;
        }

        @Override
        public Data build()
        {
            return new Date( this );
        }
    }
}
