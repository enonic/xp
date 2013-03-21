package com.enonic.wem.api.content.data;

import java.util.Objects;

import org.joda.time.DateMidnight;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.content.data.type.BaseDataType;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InconvertibleValueException;
import com.enonic.wem.api.content.data.type.JavaType;

/**
 * A generic holder for the value of a Data.
 */
public class Value
{
    private final BaseDataType type;

    private final Object object;

    private Value( final Builder builder )
    {
        Preconditions.checkNotNull( builder.type, "type cannot be null" );
        Preconditions.checkNotNull( builder.value, "value cannot be null" );
        Preconditions.checkArgument( !( builder.value instanceof Builder ), "The value of a Value cannot be: " + builder.value.getClass() );
        Preconditions.checkArgument( !( builder.value instanceof Value ), "The value of a Value cannot be: " + builder.value.getClass() );

        type = builder.type;
        object = builder.value;

        Preconditions.checkArgument( type.isValueOfExpectedJavaClass( object ), "Object expected to be of type [%s]: %s",
                                     type.getJavaType(), object.getClass().getSimpleName() );
    }

    public boolean isJavaType( Class javaType )
    {
        return javaType.isInstance( object );
    }

    public BaseDataType getType()
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

    public ContentId asContentId()
        throws InconvertibleValueException
    {
        final ContentId converted = JavaType.CONTENT_ID.convertFrom( object );
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

    public DateMidnight asDate()
        throws InconvertibleValueException
    {
        final DateMidnight converted = JavaType.DATE_MIDNIGHT.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.DATE_MIDNIGHT );
        }
        return converted;
    }

    public BinaryId asBinaryId()
        throws InconvertibleValueException
    {
        final BinaryId converted = JavaType.BINARY_ID.convertFrom( object );
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

    public static class Builder
    {
        private BaseDataType type;

        private Object value;

        public Builder type( BaseDataType value )
        {
            this.type = value;
            return this;
        }

        public Builder value( Object value )
        {
            Preconditions.checkNotNull( value, "value cannot be null" );
            Preconditions.checkArgument( !( value instanceof Builder ), "The value of a Value cannot be: " + value.getClass() );
            Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );
            this.value = value;
            return this;
        }

        public Value build()
        {
            return new Value( this );
        }

        public BaseDataType getType()
        {
            return type;
        }
    }

    public static final class Date
        extends Value
    {
        public Date( DateMidnight value )
        {
            super( newValue().type( DataTypes.DATE_MIDNIGHT ).value( value ) );
        }
    }

    public static final class WholeNumber
        extends Value
    {
        public WholeNumber( Long value )
        {
            super( newValue().type( DataTypes.WHOLE_NUMBER ).value( value ) );
        }

        public WholeNumber( Integer value )
        {
            super( newValue().type( DataTypes.WHOLE_NUMBER ).value( Long.valueOf( value ) ) );
        }

        public WholeNumber( Short value )
        {
            super( newValue().type( DataTypes.WHOLE_NUMBER ).value( Long.valueOf( value ) ) );
        }
    }

    public static final class DecimalNumber
        extends Value
    {
        public DecimalNumber( Double value )
        {
            super( newValue().type( DataTypes.DECIMAL_NUMBER ).value( value ) );
        }
    }

    public static final class Text
        extends Value
    {
        public Text( String value )
        {
            super( newValue().type( DataTypes.TEXT ).value( value ) );
        }
    }

    public static final class Xml
        extends Value
    {
        public Xml( String value )
        {
            super( newValue().type( DataTypes.XML ).value( value ) );
        }
    }

    public static final class HtmlPart
        extends Value
    {
        public HtmlPart( String value )
        {
            super( newValue().type( DataTypes.HTML_PART ).value( value ) );
        }
    }

    public static final class GeographicCoordinate
        extends Value
    {
        public GeographicCoordinate( String value )
        {
            super( newValue().type( DataTypes.GEOGRAPHIC_COORDINATE ).value( value ) );
        }
    }

    public static final class ContentReference
        extends Value
    {
        public ContentReference( ContentId value )
        {
            super( newValue().type( DataTypes.CONTENT_REFERENCE ).value( value ) );
        }
    }

    public static final class BinaryReference
        extends Value
    {
        public BinaryReference( BinaryId value )
        {
            super( newValue().type( DataTypes.BINARY_ID ).value( value ) );
        }
    }
}
