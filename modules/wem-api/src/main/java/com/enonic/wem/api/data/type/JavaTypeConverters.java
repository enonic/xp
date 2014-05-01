package com.enonic.wem.api.data.type;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.serializer.RootDataSetJsonSerializer;

public final class JavaTypeConverters
{
    private final static DateTimeFormatter DATE_MIDNIGHT_FORMATTER = new DateTimeFormatterBuilder().
        appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).toFormatter();

    private final static DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().
        appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).
        appendLiteral( "T" ).appendHourOfDay( 2 ).appendLiteral( ":" ).appendMinuteOfHour( 2 ).appendLiteral( ":" ).appendSecondOfMinute(
        2 ).toFormatter();

    private final static RootDataSetJsonSerializer DATA_SERIALIZER = new RootDataSetJsonSerializer();

    public final static JavaTypeConverter<java.lang.String> STRING = newString();

    public final static JavaTypeConverter<java.lang.Long> LONG = newLong();

    public final static JavaTypeConverter<java.lang.Double> DOUBLE = newDouble();

    public final static JavaTypeConverter<java.lang.Boolean> BOOLEAN = newBoolean();

    public final static JavaTypeConverter<RootDataSet> DATA = newData();

    public final static JavaTypeConverter<com.enonic.wem.api.content.ContentId> CONTENT_ID = newContentId();

    public final static JavaTypeConverter<com.enonic.wem.api.entity.EntityId> ENTITY_ID = newEntityId();

    public final static JavaTypeConverter<org.joda.time.DateTime> DATE_TIME = newDateTime();

    public final static JavaTypeConverter<org.joda.time.DateMidnight> DATE_MIDNIGHT = newDateMidnight();

    private static JavaTypeConverter<java.lang.String> newString()
    {
        return new JavaTypeConverter<java.lang.String>( java.lang.String.class )
        {
            @Override
            public java.lang.String convertFrom( final Object value )
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
                else if ( value instanceof java.lang.Boolean )
                {
                    return value.toString();
                }
                else if ( value instanceof com.enonic.wem.api.content.ContentId )
                {
                    return value.toString();
                }
                else if ( value instanceof org.joda.time.DateTime )
                {
                    return DATE_TIME_FORMATTER.print( (org.joda.time.DateTime) value );
                }
                else if ( value instanceof org.joda.time.DateMidnight )
                {
                    return DATE_MIDNIGHT_FORMATTER.print( (org.joda.time.DateMidnight) value );
                }
                else if ( value instanceof com.enonic.wem.api.entity.EntityId )
                {
                    return value.toString();
                }
                else if ( value instanceof com.enonic.wem.api.data.RootDataSet )
                {
                    return DATA_SERIALIZER.serializeToString( (com.enonic.wem.api.data.RootDataSet) value );
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
        };
    }

    private static JavaTypeConverter<java.lang.Long> newLong()
    {
        return new JavaTypeConverter<java.lang.Long>( java.lang.Long.class )
        {
            @Override
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
        };
    }

    private static JavaTypeConverter<java.lang.Double> newDouble()
    {
        return new JavaTypeConverter<java.lang.Double>( java.lang.Double.class )
        {
            @Override
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
        };
    }

    private static JavaTypeConverter<java.lang.Boolean> newBoolean()
    {
        return new JavaTypeConverter<java.lang.Boolean>( java.lang.Boolean.class )
        {
            @Override
            public java.lang.Boolean convertFrom( final Object value )
            {
                if ( value instanceof java.lang.Boolean )
                {
                    return (java.lang.Boolean) value;
                }
                else if ( value instanceof java.lang.String )
                {
                    return java.lang.Boolean.parseBoolean( (java.lang.String) value );
                }

                return null;
            }

            @Override
            public java.lang.Boolean convertFromString( final java.lang.String value )
            {
                return java.lang.Boolean.parseBoolean( value );
            }
        };
    }

    private static JavaTypeConverter<RootDataSet> newData()
    {
        return new JavaTypeConverter<RootDataSet>( RootDataSet.class )
        {
            @Override
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
        };
    }

    private static JavaTypeConverter<com.enonic.wem.api.content.ContentId> newContentId()
    {
        return new JavaTypeConverter<com.enonic.wem.api.content.ContentId>( com.enonic.wem.api.content.ContentId.class )
        {
            @Override
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

            @Override
            public com.enonic.wem.api.content.ContentId convertFromString( final java.lang.String value )
            {
                return com.enonic.wem.api.content.ContentId.from( value );
            }
        };
    }

    private static JavaTypeConverter<com.enonic.wem.api.entity.EntityId> newEntityId()
    {
        return new JavaTypeConverter<com.enonic.wem.api.entity.EntityId>( com.enonic.wem.api.entity.EntityId.class )
        {
            @Override
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

            @Override
            public com.enonic.wem.api.entity.EntityId convertFromString( final java.lang.String value )
            {
                return com.enonic.wem.api.entity.EntityId.from( value );
            }
        };
    }

    private static JavaTypeConverter<org.joda.time.DateTime> newDateTime()
    {
        return new JavaTypeConverter<org.joda.time.DateTime>( org.joda.time.DateTime.class )
        {
            @Override
            public org.joda.time.DateTime convertFrom( final Object value )
            {
                try
                {
                    if ( value instanceof org.joda.time.DateMidnight )
                    {
                        return ( (org.joda.time.DateMidnight) value ).toDateTime();
                    }
                    if ( value instanceof org.joda.time.DateTime )
                    {
                        return (org.joda.time.DateTime) value;
                    }
                    else if ( value instanceof java.lang.String )
                    {
                        return DATE_TIME_FORMATTER.parseDateTime( (java.lang.String) value ).toDateTime();
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

            @Override
            public org.joda.time.DateTime convertFromString( final java.lang.String value )
            {
                return DATE_TIME_FORMATTER.parseDateTime( value );
            }
        };
    }

    private static JavaTypeConverter<org.joda.time.DateMidnight> newDateMidnight()
    {
        return new JavaTypeConverter<org.joda.time.DateMidnight>( org.joda.time.DateMidnight.class )
        {
            @Override
            public org.joda.time.DateMidnight convertFrom( final Object value )
            {
                if ( value instanceof org.joda.time.DateMidnight )
                {
                    return (org.joda.time.DateMidnight) value;
                }
                else if ( value instanceof java.lang.String )
                {
                    return DATE_MIDNIGHT_FORMATTER.parseDateTime( (java.lang.String) value ).toDateMidnight();
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

            @Override
            public org.joda.time.DateMidnight convertFromString( final java.lang.String value )
            {
                return DATE_MIDNIGHT_FORMATTER.parseDateTime( value ).toDateMidnight();
            }
        };
    }
}
