package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;

public class AllUserData
{
    private Set<String> stringValues = Sets.newHashSet();

    private Set<Number> numberValues = Sets.newHashSet();

    private Set<Date> dateValues = Sets.newHashSet();

    public void addValue( Object value )
    {
        if ( value == null )
        {
            return;
        }

        if ( value instanceof Object[] )
        {
            Object[] arrayValues = (Object[]) value;

            for ( Object arrayValue : arrayValues )
            {
                addValue( arrayValue );
            }
        }
        else if ( value instanceof Number )
        {
            numberValues.add( (Number) value );
        }
        else if ( value instanceof Date )
        {
            dateValues.add( (Date) value );
        }
        else
        {
            stringValues.add( value.toString() );
        }
    }

    public Set<String> getStringValues()
    {
        return stringValues;
    }

    public Set<Number> getNumberValues()
    {
        return numberValues;
    }

    public Set<Date> getDateValues()
    {
        return dateValues;
    }
}
