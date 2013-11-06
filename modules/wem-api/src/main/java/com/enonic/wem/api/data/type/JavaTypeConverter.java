package com.enonic.wem.api.data.type;


import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.enonic.wem.api.data.serializer.RootDataSetJsonSerializer;

public abstract class JavaTypeConverter<T>
{
    Class type;

    JavaTypeConverter( final Class type )
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

    /**
     * Attempts to convert given object to this type.
     */
    public abstract T convertFrom( Object value );


    /**
     * Attempts to convert given String to this type.
     */
    public abstract T convertFromString( java.lang.String value );

    @Override
    public java.lang.String toString()
    {
        return type.getSimpleName();
    }

    public static final class String
        extends JavaTypeConverter<java.lang.String>
    {

        public static final String GET = new String();

        private String()
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
            else if ( value instanceof Boolean )
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
            else if ( value instanceof org.joda.time.DateTime )
            {
                return DateTime.FORMATTER.print( (org.joda.time.DateTime) value );
            }
            else if ( value instanceof org.joda.time.DateMidnight )
            {
                return DateMidnight.FORMATTER.print( (org.joda.time.DateMidnight) value );
            }
            else if ( value instanceof com.enonic.wem.api.entity.EntityId )
            {
                return value.toString();
            }
            else if ( value instanceof com.enonic.wem.api.data.RootDataSet )
            {
                return Data.DATA_SERIALIZER.serializeToString( (com.enonic.wem.api.data.RootDataSet) value );
            }
            else
            {
                return null;
            }
        }

        @Override
        public java.lang.String convertFromString( final java.lang.String value )
        {
            return value;
        }
    }

    public static final class Long
        extends JavaTypeConverter<java.lang.Long>
    {
        public static final Long GET = new Long();

        private Long()
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
        public java.lang.Long convertFromString( final java.lang.String value )
        {
            return new java.lang.Long( value );
        }
    }

    public static final class Double
        extends JavaTypeConverter<java.lang.Double>
    {
        public static final Double GET = new Double();

        private Double()
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
                try
                {
                    return java.lang.Double.parseDouble( (java.lang.String) value );
                }
                catch ( NumberFormatException e )
                {
                    return null;
                }
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
        public java.lang.Double convertFromString( final java.lang.String value )
        {
            return new java.lang.Double( value );
        }

    }

    public static final class DateMidnight
        extends JavaTypeConverter<org.joda.time.DateMidnight>
    {
        private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().
            appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).toFormatter();

        public static final DateMidnight GET = new DateMidnight();

        private DateMidnight()
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

        public org.joda.time.DateMidnight convertFromString( final java.lang.String value )
        {
            return FORMATTER.parseDateTime( value ).toDateMidnight();
        }
    }

    public static final class DateTime
        extends JavaTypeConverter<org.joda.time.DateTime>
    {
        private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().
            appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).
            appendLiteral( "T" ).appendHourOfDay( 2 ).appendLiteral( ":" ).appendMinuteOfHour( 2 ).appendLiteral(
            ":" ).appendSecondOfMinute( 2 ).toFormatter();

        public static final DateTime GET = new DateTime();

        private DateTime()
        {
            super( org.joda.time.DateTime.class );
        }

        public org.joda.time.DateTime convertFrom( final Object value )
        {
            try
            {
                if ( value instanceof org.joda.time.DateMidnight )
                {
                    return (org.joda.time.DateTime) ( (org.joda.time.DateMidnight) value ).toDateTime();
                }
                if ( value instanceof org.joda.time.DateTime )
                {
                    return (org.joda.time.DateTime) value;
                }
                else if ( value instanceof java.lang.String )
                {
                    return FORMATTER.parseDateTime( (java.lang.String) value ).toDateTime();
                }
                else if ( value instanceof java.lang.Long )
                {
                    return new org.joda.time.DateTime( value );
                }
                else
                {
                    return null;
                }
            }
            catch ( IllegalArgumentException e )
            {
                return null;
            }
        }

        public org.joda.time.DateTime convertFromString( final java.lang.String value )
        {
            return FORMATTER.parseDateTime( value ).toDateTime();
        }
    }

    public static final class ContentId
        extends JavaTypeConverter<com.enonic.wem.api.content.ContentId>
    {
        public static final ContentId GET = new ContentId();

        private ContentId()
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

        public com.enonic.wem.api.content.ContentId convertFromString( final java.lang.String value )
        {
            return com.enonic.wem.api.content.ContentId.from( value );
        }
    }

    public static final class EntityId
        extends JavaTypeConverter<com.enonic.wem.api.entity.EntityId>
    {
        public static final EntityId GET = new EntityId();

        private EntityId()
        {
            super( com.enonic.wem.api.entity.EntityId.class );
        }

        public com.enonic.wem.api.entity.EntityId convertFrom( final Object value )
        {
            if ( value instanceof com.enonic.wem.api.entity.EntityId )
            {
                return (com.enonic.wem.api.entity.EntityId) value;
            }
            else if ( value instanceof java.lang.String )
            {
                return com.enonic.wem.api.entity.EntityId.from( (java.lang.String) value );
            }
            else
            {
                return null;
            }
        }

        public com.enonic.wem.api.entity.EntityId convertFromString( final java.lang.String value )
        {
            return com.enonic.wem.api.entity.EntityId.from( value );
        }
    }

    public static final class BinaryId
        extends JavaTypeConverter<com.enonic.wem.api.content.binary.BinaryId>
    {
        public static final BinaryId GET = new BinaryId();

        private BinaryId()
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

        public com.enonic.wem.api.content.binary.BinaryId convertFromString( final java.lang.String value )
        {
            return com.enonic.wem.api.content.binary.BinaryId.from( value );
        }
    }

    public static final class Data
        extends JavaTypeConverter<com.enonic.wem.api.data.RootDataSet>
    {
        private final static RootDataSetJsonSerializer DATA_SERIALIZER = new RootDataSetJsonSerializer();

        public static final Data GET = new Data();

        private Data()
        {
            super( com.enonic.wem.api.data.RootDataSet.class );
        }

        public com.enonic.wem.api.data.RootDataSet convertFrom( Object value )
        {
            if ( value instanceof com.enonic.wem.api.data.RootDataSet )
            {
                return (com.enonic.wem.api.data.RootDataSet) value;
            }
            else if ( value instanceof java.lang.String )
            {
                return DATA_SERIALIZER.parse( (java.lang.String) value );
            }
            else
            {
                return null;
            }
        }

        @Override
        public com.enonic.wem.api.data.RootDataSet convertFromString( final java.lang.String value )
        {
            return DATA_SERIALIZER.parse( value );
        }
    }
}