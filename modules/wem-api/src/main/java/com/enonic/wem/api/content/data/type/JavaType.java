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

    public static final ContentId CONTENT_ID = new ContentId();

    public static final BinaryId BINARY_ID = new BinaryId();

    public static final DataSet DATA_SET = new DataSet();

    public static final Map<java.lang.Class, BaseType> INSTANCES = new LinkedHashMap<>();

    static
    {
        INSTANCES.put( DATA_SET.getType(), DATA_SET );
        INSTANCES.put( BINARY_ID.getType(), BINARY_ID );
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

    public static abstract class BaseType<T>
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

        public abstract T convertFrom( Object value );

        public abstract T convertFrom( java.lang.String value );

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
            else if ( value instanceof com.enonic.wem.api.content.binary.BinaryId )
            {
                return value.toString();
            }
            else if ( value instanceof com.enonic.wem.api.content.ContentId )
            {
                return value.toString();
            }
            else
            {
                return null;
            }
        }

        @Override
        public java.lang.String convertFrom( final java.lang.String value )
        {
            return value;
        }
    }

    public final static class Long
        extends BaseType<java.lang.Long>
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
            else if ( value instanceof java.lang.Integer )
            {
                return java.lang.Long.valueOf( (java.lang.Integer) value );
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

        @Override
        public java.lang.Long convertFrom( final java.lang.String value )
        {
            return new java.lang.Long( value );
        }
    }

    public final static class Double
        extends BaseType<java.lang.Double>
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

        @Override
        public java.lang.Double convertFrom( final java.lang.String value )
        {
            return new java.lang.Double( value );
        }

    }

    public final static class DateMidnight
        extends BaseType<org.joda.time.DateMidnight>
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

        public org.joda.time.DateMidnight convertFrom( final java.lang.String value )
        {
            return FORMATTER.parseDateTime( value ).toDateMidnight();
        }
    }

    public final static class ContentId
        extends BaseType<com.enonic.wem.api.content.ContentId>
    {
        ContentId()
        {
            super( com.enonic.wem.api.content.ContentId.class );
        }

        public com.enonic.wem.api.content.ContentId convertFrom( final Object value )
        {
            if ( value instanceof com.enonic.wem.api.content.ContentId )
            {
                return (com.enonic.wem.api.content.ContentId) value;
            }
            else if ( value instanceof java.lang.String )
            {
                return com.enonic.wem.api.content.ContentId.from( (java.lang.String) value );
            }
            else
            {
                return null;
            }
        }

        public com.enonic.wem.api.content.ContentId convertFrom( final java.lang.String value )
        {
            return com.enonic.wem.api.content.ContentId.from( value );
        }
    }

    public final static class BinaryId
        extends BaseType<com.enonic.wem.api.content.binary.BinaryId>
    {
        BinaryId()
        {
            super( com.enonic.wem.api.content.binary.BinaryId.class );
        }

        public com.enonic.wem.api.content.binary.BinaryId convertFrom( Object value )
        {
            if ( value instanceof com.enonic.wem.api.content.binary.BinaryId )
            {
                return (com.enonic.wem.api.content.binary.BinaryId) value;
            }
            else if ( value instanceof java.lang.String )
            {
                return com.enonic.wem.api.content.binary.BinaryId.from( (java.lang.String) value );
            }
            else
            {
                return null;
            }
        }

        public com.enonic.wem.api.content.binary.BinaryId convertFrom( final java.lang.String value )
        {
            return com.enonic.wem.api.content.binary.BinaryId.from( value );
        }
    }


    public final static class DataSet
        extends BaseType<com.enonic.wem.api.content.data.DataSet>
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

        @Override
        public com.enonic.wem.api.content.data.DataSet convertFrom( final java.lang.String value )
        {
            throw new UnsupportedOperationException( "A DataSet cannot be converted from a String" );
        }
    }
}
