package com.enonic.wem.query;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public abstract class ValueExpr<T>
    implements Expression
{
    protected final T value;

    private ValueExpr( final T value )
    {
        this.value = value;
    }

    public final T getValue()
    {
        return this.value;
    }

    public abstract String getValueAsString();

    public boolean isString()
    {
        return false;
    }

    public boolean isNumber()
    {
        return false;
    }

    public boolean isDate()
    {
        return false;
    }

    public boolean isGeoCoordinate()
    {
        return false;
    }

    public final static class StringValue
        extends ValueExpr<String>
    {
        private StringValue( final String value )
        {
            super( value );
        }

        @Override
        public boolean isString()
        {
            return true;
        }

        @Override
        public String getValueAsString()
        {
            return getValue();
        }

        @Override
        public String toString()
        {
            return "\"" + getValueAsString() + "\"";
        }
    }

    public final static class NumberValue
        extends ValueExpr<Double>
    {
        private NumberValue( final Double value )
        {
            super( value );
        }

        @Override
        public boolean isNumber()
        {
            return true;
        }

        @Override
        public String getValueAsString()
        {
            return String.valueOf( getValue() );
        }

        @Override
        public String toString()
        {
            return getValueAsString();
        }
    }

    public final static class DateValue
        extends ValueExpr<DateTime>
    {
        private DateValue( final DateTime value )
        {
            super( value );
        }

        @Override
        public boolean isDate()
        {
            return true;
        }

        @Override
        public String getValueAsString()
        {
            return ISODateTimeFormat.basicDateTime().print( getValue() );
        }

        @Override
        public String toString()
        {
            return "date(\"" + getValueAsString() + "\")";
        }
    }

    public final static class GeoPointValue
        extends ValueExpr<String>
    {
        private GeoPointValue( final String value )
        {
            super( value );
        }

        @Override
        public boolean isGeoCoordinate()
        {
            return true;
        }

        @Override
        public String getValueAsString()
        {
            return this.value;
        }

        @Override
        public String toString()
        {
            return "geoPoint(\"" + getValueAsString() + "\")";
        }
    }


    public static ValueExpr<String> string( final String value )
    {
        return new StringValue( value );
    }

    public static ValueExpr<DateTime> date( final String value )
    {
        return new DateValue( ISODateTimeFormat.basicDate().parseDateTime( value ) );
    }

    public static ValueExpr<Double> number( final String value )
    {
        return new NumberValue( Double.parseDouble( value ) );
    }

    public static ValueExpr<String> geoCoordinate( final String value )
    {
        return new GeoPointValue( value );
    }
}
