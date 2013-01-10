package com.enonic.wem.api.content.datatype;


import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.enonic.wem.api.blob.BlobKey;


public final class JavaType
{
    public static final String STRING = new String();

    public static final Double DOUBLE = new Double();

    public static final Long LONG = new Long();

    public static final Date DATE = new Date();

    public static final Blob BLOB = new Blob();

    public static final DataSet DATA_SET = new DataSet();

    public static final Map<java.lang.Class, BaseType> INSTANCES = new LinkedHashMap<Class, BaseType>();

    static
    {
        INSTANCES.put( DATA_SET.getType(), DATA_SET );
        INSTANCES.put( BLOB.getType(), BLOB );
        INSTANCES.put( STRING.getType(), STRING );
        INSTANCES.put( DOUBLE.getType(), DOUBLE );
        INSTANCES.put( LONG.getType(), LONG );
        INSTANCES.put( DATE.getType(), DATE );
    }

    public static BaseType resolveType( Object o )
    {
        for ( BaseType type : INSTANCES.values() )
        {
            if ( type.getType().isInstance( o ) )
            {
                return type;
            }
        }
        return null;
    }

    static class BaseType
    {
        Class type;

        BaseType( final Class type )
        {
            this.type = type;
        }

        public Class getType()
        {
            return type;
        }

        public boolean isInstance( final Object value )
        {
            return type.isInstance( value );
        }

        @Override
        public java.lang.String toString()
        {
            return type.getSimpleName();
        }
    }

    public final static class String
        extends BaseType
    {
        String()
        {
            super( java.lang.String.class );
        }

        public java.lang.String convertFrom( Object value )
        {
            if ( value instanceof java.lang.String )
            {
                return (java.lang.String) value;
            }
            else if ( value instanceof java.lang.Long )
            {
                return value.toString();
            }
            else if ( value instanceof java.lang.Double )
            {
                return value.toString();
            }
            else if ( value instanceof BlobKey )
            {
                return value.toString();
            }
            else
            {
                return null;
            }
        }
    }

    public final static class Long
        extends BaseType
    {
        Long()
        {
            super( java.lang.Long.class );
        }

        public java.lang.Long convertFrom( Object value )
        {
            if ( value instanceof java.lang.Long )
            {
                return (java.lang.Long) value;
            }
            else if ( value instanceof java.lang.String )
            {
                return new java.lang.Long( (java.lang.String) value );
            }
            else if ( value instanceof java.lang.Double )
            {
                return ( (java.lang.Double) value ).longValue();
            }
            else
            {
                return null;
            }
        }
    }

    public final static class Double
        extends BaseType
    {
        Double()
        {
            super( java.lang.Double.class );
        }

        public java.lang.Double convertFrom( Object value )
        {
            if ( value instanceof java.lang.Double )
            {
                return (java.lang.Double) value;
            }
            else if ( value instanceof java.lang.String )
            {
                return new java.lang.Double( (java.lang.String) value );
            }
            else if ( value instanceof java.lang.Long )
            {
                return ( (java.lang.Long) value ).doubleValue();
            }
            else
            {
                return null;
            }
        }

    }

    public final static class Date
        extends BaseType
    {
        private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().
            appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).toFormatter();

        Date()
        {
            super( DateMidnight.class );
        }

        public DateMidnight convertFrom( Object value )
        {
            if ( value instanceof java.lang.String )
            {
                return FORMATTER.parseDateTime( (java.lang.String) value ).toDateMidnight();
            }
            else if ( value instanceof java.lang.Long )
            {
                return new DateMidnight( value );
            }
            else
            {
                return null;
            }
        }
    }

    public final static class Blob
        extends BaseType
    {
        Blob()
        {
            super( BlobKey.class );
        }

        public BlobKey convertFrom( Object value )
        {
            if ( value instanceof BlobKey )
            {
                return (BlobKey) value;
            }
            else
            {
                return null;
            }
        }
    }


    public final static class DataSet
        extends BaseType
    {
        DataSet()
        {
            super( com.enonic.wem.api.content.data.DataSet.class );
        }

        public com.enonic.wem.api.content.data.DataSet convertFrom( Object value )
        {
            if ( value instanceof com.enonic.wem.api.content.data.DataSet )
            {
                return (com.enonic.wem.api.content.data.DataSet) value;
            }
            else
            {
                return null;
            }
        }
    }
}
