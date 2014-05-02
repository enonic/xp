package com.enonic.wem.api.data.type;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Value;

/**
 * ValueTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public abstract class ValueType<T>
{
    private final int key;

    private final String name;

    private final Class classType;

    private final JavaTypeConverter<T> javaTypeConverter;

    public ValueType( final int key, final String name, final JavaTypeConverter<T> javaTypeConverter )
    {
        this.key = key;
        this.name = name;
        this.classType = javaTypeConverter.getClass();
        this.javaTypeConverter = javaTypeConverter;
    }

    public int getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public Class getClassType()
    {
        return classType;
    }

    public JavaTypeConverter getJavaTypeConverter()
    {
        return this.javaTypeConverter;
    }

    /**
     * Checks by default if given property's value is of correct Java class.
     * Can be overridden by concrete classes to do extensive validation.
     *
     * @param value the value to check the validity of
     */
    public void checkValidity( final Value value )
    {
        checkValueIsOfExpectedClass( value );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ValueType ) )
        {
            return false;
        }

        final ValueType that = (ValueType) o;
        return key == that.key;
    }

    @Override
    public int hashCode()
    {
        return key;
    }

    @Override
    public java.lang.String toString()
    {
        return name;
    }

    public boolean isValueOfExpectedClass( final Value value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return this.javaTypeConverter.isInstance( value.getObject() );
    }

    public void checkValueIsOfExpectedClass( final Value value )
    {
        if ( !isValueOfExpectedClass( value ) )
        {
            throw new ValueTypeException( "Value object is not of expected class. Expected [%s], got [%s]", getClassType(),
                                          value.getType().getClassType() );
        }
    }

    /**
     * Attempts to convert given object to this type.
     */
    public T convert( final Object object )
    {
        final T value = this.javaTypeConverter.convertFrom( object );
        if ( value != null )
        {
            return value;
        }

        throw new ValueTypeException( "Value of type [%s] cannot be converted to [%s]", object.getClass(), getName() );
    }

    /**
     * Attempts to convert given java.lang.String to this type.
     */
    public T convert( final String object )
    {
        return this.javaTypeConverter.convertFromString( object );
    }

    public abstract Value newValue( Object value );
}
