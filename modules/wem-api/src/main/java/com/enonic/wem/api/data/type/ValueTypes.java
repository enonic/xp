package com.enonic.wem.api.data.type;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.util.GeoPoint;

public final class ValueTypes
{
    public static final ValueType<RootDataSet> DATA = newData();

    public static final ValueType<String> STRING = newString();

    public static final ValueType<String> HTML_PART = newHtmlPart();

    public static final ValueType<String> XML = newXml();

    public static final ValueType<DateMidnight> DATE_MIDNIGHT = newDateMidnight();

    public static final ValueType<DateTime> DATE_TIME = newDateTime();

    public static final ValueType<ContentId> CONTENT_ID = newContentId();

    public static final ValueType<Long> LONG = newLong();

    public static final ValueType<Double> DOUBLE = newDouble();

    public static final ValueType<GeoPoint> GEO_POINT = newGeoPoint();

    public static final ValueType<EntityId> ENTITY_ID = newEntityId();

    public static final ValueType<Boolean> BOOLEAN = newBoolean();

    private static final Map<Integer, ValueType> typesByKey = new HashMap<>();

    private static final Map<java.lang.String, ValueType> typesByName = new HashMap<>();

    static
    {
        register( DATA );
        register( STRING );
        register( HTML_PART );
        register( XML );
        register( DATE_MIDNIGHT );
        register( DATE_TIME );
        register( CONTENT_ID );
        register( LONG );
        register( DOUBLE );
        register( GEO_POINT );
        register( ENTITY_ID );
        register( BOOLEAN );
    }

    private static void register( ValueType valueType )
    {
        Object previous = typesByKey.put( valueType.getKey(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getKey() );

        previous = typesByName.put( valueType.getName(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getName() );
    }

    public static ValueType parseByKey( int key )
    {
        return typesByKey.get( key );
    }

    public static ValueType parseByName( java.lang.String name )
    {
        return typesByName.get( name );
    }

    private static ValueType<Boolean> newBoolean()
    {
        return new ValueType<Boolean>( 12, "Boolean", JavaTypeConverters.BOOLEAN )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newBoolean( convert( value ) );
            }
        };
    }

    private static ValueType<String> newString()
    {
        return new ValueType<String>( 1, "String", JavaTypeConverters.STRING )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newString( convert( value ) );
            }
        };
    }

    private static ValueType<RootDataSet> newData()
    {
        return new ValueType<RootDataSet>( 0, "Data", JavaTypeConverters.DATA )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newData( convert( value ) );
            }
        };
    }

    private static ValueType<String> newHtmlPart()
    {
        return new ValueType<String>( 3, "HtmlPart", JavaTypeConverters.STRING )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newHtmlPart( convert( value ) );
            }
        };
    }

    private static ValueType<Double> newDouble()
    {
        return new ValueType<Double>( 9, "Double", JavaTypeConverters.DOUBLE )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newDouble( convert( value ) );
            }
        };
    }

    private static ValueType<Long> newLong()
    {
        return new ValueType<Long>( 8, "Long", JavaTypeConverters.LONG )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newLong( convert( value ) );
            }
        };
    }

    private static ValueType<String> newXml()
    {
        return new ValueType<String>( 4, "Xml", JavaTypeConverters.STRING )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newXml( convert( value ) );
            }
        };
    }

    private static ValueType<DateMidnight> newDateMidnight()
    {
        return new ValueType<DateMidnight>( 5, "DateMidnight", JavaTypeConverters.DATE_MIDNIGHT )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newDateMidnight( convert( value ) );
            }
        };
    }

    private static ValueType<DateTime> newDateTime()
    {
        return new ValueType<DateTime>( 6, "DateTime", JavaTypeConverters.DATE_TIME )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newDateMidnight( convert( value ) );
            }
        };
    }

    private static ValueType<ContentId> newContentId()
    {
        return new ValueType<ContentId>( 7, "ContentId", JavaTypeConverters.CONTENT_ID )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newContentId( convert( value ) );
            }
        };
    }

    private static ValueType<EntityId> newEntityId()
    {
        return new ValueType<EntityId>( 11, "EntityId", JavaTypeConverters.ENTITY_ID )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newEntityId( convert( value ) );
            }
        };
    }

    private static ValueType<GeoPoint> newGeoPoint()
    {
        return new ValueType<GeoPoint>( 10, "GeoPoint", JavaTypeConverters.GEO_POINT )
        {
            @Override
            public Value newValue( final Object value )
            {
                return Value.newGeoPoint( convert( value ) );
            }
        };
    }
}
