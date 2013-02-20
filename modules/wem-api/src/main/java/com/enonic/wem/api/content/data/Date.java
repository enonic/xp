package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;

import com.enonic.wem.api.content.data.type.DataTypes;

public final class Date
    extends Data
{
    public Date( final String name, final DateMidnight value )
    {
        super( Data.newData().name( name ).value( Value.newValue().type( DataTypes.DATE ).value( value ) ) );
    }

    Date( final DateBuilder dateBuilder )
    {
        super( dateBuilder );
    }
}
