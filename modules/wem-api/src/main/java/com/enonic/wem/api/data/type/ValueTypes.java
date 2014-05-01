package com.enonic.wem.api.data.type;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;

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

    public static final ValueType<String> GEO_POINT = new GeoPointType();

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
                return new Value.Boolean( convert( value ) );
            }

            @Override
            public Property newProperty( final String name, final Value value )
            {
                return new Property.Boolean( name, value );
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

            @Override
            public Property newProperty( final String name, final Value value )
            {
                return new Property.String( name, value );
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
                return new Value.Data( convert( value ) );
            }

            @Override
            public Property newProperty( final java.lang.String name, final Value value )
            {
                return new Property.String( name, value );
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
                return new Value.HtmlPart( convert( value ) );
            }

            @Override
            public Property newProperty( final java.lang.String name, final Value value )
            {
                return new Property.HtmlPart( name, value );
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
                return new Value.Double( convert( value ) );
            }

            @Override
            public Property newProperty( final java.lang.String name, final Value value )
            {
                return new Property.Double( name, value );
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
                return new Value.Long( convert( value ) );
            }

            @Override
            public Property newProperty( final java.lang.String name, final Value value )
            {
                return new Property.Long( name, value );
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
                return new Value.Xml( convert( value ) );
            }

            @Override
            public Property newProperty( final java.lang.String name, final Value value )
            {
                return new Property.Xml( name, value );
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
                return new Value.DateMidnight( convert( value ) );
            }

            @Override
            public Property newProperty( final java.lang.String name, final Value value )
            {
                return new Property.Date( name, value );
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
                return new Value.DateMidnight( convert( value ) );
            }

            @Override
            public Property newProperty( final java.lang.String name, final Value value )
            {
                return new Property.Date( name, value );
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
                return new Value.ContentId( convert( value ) );
            }

            @Override
            public Property newProperty( final java.lang.String name, final Value value )
            {
                return new Property.ContentId( name, value );
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
                return new Value.EntityId( convert( value ) );
            }

            @Override
            public Property newProperty( final String name, final Value value )
            {
                return new Property.EntityId( name, value );
            }
        };
    }
}
