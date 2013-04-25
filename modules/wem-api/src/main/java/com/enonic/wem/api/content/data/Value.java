package com.enonic.wem.api.content.data;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.type.BasePropertyType;
import com.enonic.wem.api.content.data.type.InconvertibleValueException;
import com.enonic.wem.api.content.data.type.JavaType;
import com.enonic.wem.api.content.data.type.PropertyTypes;

/**
 * A generic holder for the value of a Data.
 */
public class Value
{
    private final BasePropertyType type;

    private final Object object;

    private Value( final BasePropertyType type, final Object value )
    {
        Preconditions.checkNotNull( type, "type cannot be null" );
        Preconditions.checkNotNull( value, "value cannot be null" );
        Preconditions.checkArgument( !( value instanceof Builder ), "The value of a Value cannot be: " + value.getClass() );
        Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );

        this.type = type;
        final boolean valueIsOfExpectedJavaClass = type.isValueOfExpectedJavaClass( value );
        if ( !valueIsOfExpectedJavaClass )
        {
            final Object converted = type.getJavaType().convertFrom( value );
            if ( converted == null )
            {
                throw new InconvertibleValueException( value, type.getJavaType() );
            }
            object = converted;
        }
        else
        {
            object = value;
        }
    }

    private Value( final Builder builder )
    {
        Preconditions.checkNotNull( builder.type, "type cannot be null" );
        Preconditions.checkNotNull( builder.value, "value cannot be null" );
        Preconditions.checkArgument( !( builder.value instanceof Builder ), "The value of a Value cannot be: " + builder.value.getClass() );
        Preconditions.checkArgument( !( builder.value instanceof Value ), "The value of a Value cannot be: " + builder.value.getClass() );

        type = builder.type;

        final boolean valueIsOfExpectedJavaClass = type.isValueOfExpectedJavaClass( builder.value );
        if ( !valueIsOfExpectedJavaClass )
        {
            final Object converted = type.getJavaType().convertFrom( builder.value );
            if ( converted == null )
            {
                throw new InconvertibleValueException( builder.value, type.getJavaType() );
            }
            object = converted;
        }
        else
        {
            object = builder.value;
        }
    }

    public boolean isJavaType( Class javaType )
    {
        return javaType.isInstance( object );
    }

    public BasePropertyType getType()
    {
        return type;
    }

    public Object getObject()
    {
        return object;
    }

    public String getString()
    {
        return (String) object;
    }

    public Long getLong()
    {
        return (Long) object;
    }

    public String asString()
        throws InconvertibleValueException
    {
        final String converted = JavaType.STRING.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.STRING );
        }
        return converted;
    }

    public com.enonic.wem.api.content.ContentId asContentId()
        throws InconvertibleValueException
    {
        final com.enonic.wem.api.content.ContentId converted = JavaType.CONTENT_ID.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.CONTENT_ID );
        }
        return converted;
    }

    public Long asLong()
        throws InconvertibleValueException
    {
        final Long converted = JavaType.LONG.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.LONG );
        }
        return converted;
    }

    public Double asDouble()
        throws InconvertibleValueException
    {
        final Double converted = JavaType.DOUBLE.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.DOUBLE );
        }
        return converted;
    }

    public org.joda.time.DateMidnight asDate()
        throws InconvertibleValueException
    {
        final org.joda.time.DateMidnight converted = JavaType.DATE_MIDNIGHT.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.DATE_MIDNIGHT );
        }
        return converted;
    }

    public com.enonic.wem.api.content.binary.BinaryId asBinaryId()
        throws InconvertibleValueException
    {
        final com.enonic.wem.api.content.binary.BinaryId converted = JavaType.BINARY_ID.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.BINARY_ID );
        }
        return converted;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Value other = (Value) o;

        return Objects.equals( type, other.type ) && Objects.equals( object, other.object );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, object );
    }

    @Override
    public String toString()
    {
        return String.valueOf( object );
    }

    public static Builder newValue()
    {
        return new Builder();
    }

    public Property newData( final String name )
    {
        return getType().newProperty( name, this );
    }

    public static class Builder
        extends AbstractValueBuilder
    {
        private BasePropertyType type;

        private Object value;

        public <T> AbstractValueBuilder type( BasePropertyType type )
        {
            this.type = type;
            return type.newValueBuilder();
        }

        public Value value( Object value )
        {
            Preconditions.checkNotNull( type, "type must be set before value" );
            Preconditions.checkNotNull( value, "value cannot be null" );
            Preconditions.checkArgument( !( value instanceof Builder ), "The value of a Value cannot be: " + value.getClass() );
            Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );
            this.value = value;
            return this.build();
        }

        public Value build()
        {
            return new Value( this );
        }

        public BasePropertyType getType()
        {
            return type;
        }
    }

    public abstract static class AbstractValueBuilder<T extends Value, O>
    {
        public abstract T value( final O value );
    }

    public static final class Date
        extends Value
    {
        public Date( org.joda.time.DateMidnight value )
        {
            super( PropertyTypes.DATE_MIDNIGHT, value );
        }

        public Date( final String value )
        {
            super( PropertyTypes.DATE_MIDNIGHT, JavaType.DATE_MIDNIGHT.convertFrom( value ) );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<Date, org.joda.time.DateMidnight>
        {
            public Date value( final org.joda.time.DateMidnight value )
            {
                return new Date( value );
            }
        }
    }

    public static final class WholeNumber
        extends Value
    {
        public WholeNumber( Long value )
        {
            super( PropertyTypes.WHOLE_NUMBER, value );
        }

        public WholeNumber( Integer value )
        {
            super( PropertyTypes.WHOLE_NUMBER, Long.valueOf( value ) );
        }

        public WholeNumber( Short value )
        {
            super( PropertyTypes.WHOLE_NUMBER, Long.valueOf( value ) );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<WholeNumber, Long>
        {
            public WholeNumber value( final Long value )
            {
                return new WholeNumber( value );
            }
        }
    }

    public static final class DecimalNumber
        extends Value
    {
        public DecimalNumber( Double value )
        {
            super( PropertyTypes.DECIMAL_NUMBER, value );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<DecimalNumber, Double>
        {
            public DecimalNumber value( final Double value )
            {
                return new DecimalNumber( value );
            }
        }
    }

    public static final class Text
        extends Value
    {
        public Text( String value )
        {
            super( PropertyTypes.TEXT, value );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<Text, String>
        {
            public Text value( final String value )
            {
                return new Text( value );
            }
        }
    }

    public static final class Xml
        extends Value
    {
        public Xml( String value )
        {
            super( PropertyTypes.XML, value );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<Xml, java.lang.String>
        {
            public Xml value( final java.lang.String value )
            {
                return new Xml( value );
            }
        }
    }

    public static final class HtmlPart
        extends Value
    {
        public HtmlPart( String value )
        {
            super( PropertyTypes.HTML_PART, value );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<HtmlPart, java.lang.String>
        {
            public HtmlPart value( final java.lang.String value )
            {
                return new HtmlPart( value );
            }
        }
    }

    public static final class GeographicCoordinate
        extends Value
    {
        public GeographicCoordinate( String value )
        {
            super( PropertyTypes.GEOGRAPHIC_COORDINATE, value );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<GeographicCoordinate, java.lang.String>
        {
            public GeographicCoordinate value( final java.lang.String value )
            {
                return new GeographicCoordinate( value );
            }
        }
    }

    public static final class ContentId
        extends Value
    {
        public ContentId( com.enonic.wem.api.content.ContentId value )
        {
            super( PropertyTypes.CONTENT_ID, value );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<ContentId, com.enonic.wem.api.content.ContentId>
        {
            public ContentId value( final com.enonic.wem.api.content.ContentId value )
            {
                return new ContentId( value );
            }
        }
    }

    public static final class BinaryId
        extends Value
    {
        public BinaryId( com.enonic.wem.api.content.binary.BinaryId value )
        {
            super( PropertyTypes.BINARY_ID, value );
        }

        public static class ValueBuilder
            extends AbstractValueBuilder<BinaryId, com.enonic.wem.api.content.binary.BinaryId>
        {
            public BinaryId value( final com.enonic.wem.api.content.binary.BinaryId value )
            {
                return new BinaryId( value );
            }
        }
    }
}
