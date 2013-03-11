package com.enonic.wem.api.content.data.type;


import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;


public final class JavaType
{
    public static final String STRING = new String();

    public static final Double DOUBLE = new Double();

    public static final Long LONG = new Long();

    public static final DateMidnight DATE_MIDNIGHT = new DateMidnight();

    public static final BlobKey BLOB_KEY = new BlobKey();

    public static final DataSet DATA_SET = new DataSet();

    public static final Map<java.lang.Class, BaseType> INSTANCES = new LinkedHashMap<>();

    static
    {
        INSTANCES.put( DATA_SET.getType(), DATA_SET );
        INSTANCES.put( BLOB_KEY.getType(), BLOB_KEY );
        INSTANCES.put( STRING.getType(), STRING );
        INSTANCES.put( DOUBLE.getType(), DOUBLE );
        INSTANCES.put( LONG.getType(), LONG );
        INSTANCES.put( DATE_MIDNIGHT.getType(), DATE_MIDNIGHT );
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
            else if ( value instanceof com.enonic.wem.api.blob.BlobKey )
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

    public final static class DateMidnight
        extends BaseType
    {
        private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().
            appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).toFormatter();

        DateMidnight()
        {
            super( org.joda.time.DateMidnight.class );
        }

        public org.joda.time.DateMidnight convertFrom( final Object value )
        {
            if ( value instanceof org.joda.time.DateMidnight )
            {
                return (org.joda.time.DateMidnight) value;
            }
            else if ( value instanceof java.lang.String )
            {
                return FORMATTER.parseDateTime( (java.lang.String) value ).toDateMidnight();
            }
            else if ( value instanceof java.lang.Long )
            {
                return new org.joda.time.DateMidnight( value );
            }
            else
            {
                return null;
            }
        }

        public org.joda.time.DateMidnight convert( final java.lang.String value )
        {
            return FORMATTER.parseDateTime( value ).toDateMidnight();
        }
    }

    public final static class BlobKey
        extends BaseType
    {
        BlobKey()
        {
            super( com.enonic.wem.api.blob.BlobKey.class );
        }

        public com.enonic.wem.api.blob.BlobKey convertFrom( Object value )
        {
            if ( value instanceof com.enonic.wem.api.blob.BlobKey )
            {
                return (com.enonic.wem.api.blob.BlobKey) value;
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
