package com.enonic.wem.api.content.data.type;


import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;


public final class JavaTypeConverters
{
    public static final StringConverter STRING_CONVERTER = new StringConverter();

    public static final DoubleConverter DOUBLE_CONVERTER = new DoubleConverter();

    public static final LongConverter LONG_CONVERTER = new LongConverter();

    public static final DateMidnightConverter DATE_MIDNIGHT_CONVERTER = new DateMidnightConverter();

    public static final ContentIdConverter CONTENT_ID_CONVERTER = new ContentIdConverter();

    public static final BinaryIdConverter BINARY_ID_CONVERTER = new BinaryIdConverter();

    public static final Map<java.lang.Class, JavaTypeConverter> INSTANCES = new LinkedHashMap<>();

    static
    {
        INSTANCES.put( BINARY_ID_CONVERTER.getType(), BINARY_ID_CONVERTER );
        INSTANCES.put( STRING_CONVERTER.getType(), STRING_CONVERTER );
        INSTANCES.put( DOUBLE_CONVERTER.getType(), DOUBLE_CONVERTER );
        INSTANCES.put( LONG_CONVERTER.getType(), LONG_CONVERTER );
        INSTANCES.put( DATE_MIDNIGHT_CONVERTER.getType(), DATE_MIDNIGHT_CONVERTER );
    }

    public static JavaTypeConverter resolveConverter( Object o )
    {
        for ( JavaTypeConverter type : INSTANCES.values() )
        {
            if ( type.getType().isInstance( o ) )
            {
                return type;
            }
        }
        return null;
    }

    public static abstract class JavaTypeConverter<T>
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

        public abstract T convertFrom( Object value );

        public abstract T convertFrom( java.lang.String value );

        @Override
        public java.lang.String toString()
        {
            return type.getSimpleName();
        }
    }

    public final static class StringConverter
        extends JavaTypeConverter
    {
        StringConverter()
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

    public final static class LongConverter
        extends JavaTypeConverter<java.lang.Long>
    {
        LongConverter()
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

    public final static class DoubleConverter
        extends JavaTypeConverter<java.lang.Double>
    {
        DoubleConverter()
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

    public final static class DateMidnightConverter
        extends JavaTypeConverter<org.joda.time.DateMidnight>
    {
        private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().
            appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).toFormatter();

        DateMidnightConverter()
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

    public final static class ContentIdConverter
        extends JavaTypeConverter<com.enonic.wem.api.content.ContentId>
    {
        ContentIdConverter()
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

    public final static class BinaryIdConverter
        extends JavaTypeConverter<com.enonic.wem.api.content.binary.BinaryId>
    {
        BinaryIdConverter()
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

}
