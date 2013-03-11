package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;

import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.JavaType;

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

        public DateBuilder value( final String value )
        {
            setValue( JavaType.DATE_MIDNIGHT.convert( value ) );
            return this;
        }

        @Override
        public Date build()
        {
            return new Date( this );
        }
    }
}
