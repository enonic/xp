package com.enonic.wem.api.content.data.type;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

/**
 * ValueTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public abstract class ValueType
{
    private final int key;

    private final String name;

    private JavaType.BaseType javaType;

    public ValueType( final int key, final JavaType.BaseType javaType )
    {
        this.key = key;
        this.name = this.getClass().getSimpleName();
        this.javaType = javaType;
    }

    public int getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public JavaType.BaseType getJavaType()
    {
        return this.javaType;
    }

    /**
     * Checks by default if given property's value is of correct Java class.
     * Can be overridden by concrete classes to do extensive validation.
     *
     * @param property the property to check the validity of
     * @throws InvalidValueTypeException
     */
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        checkValueIsOfExpectedJavaClass( property );
    }

    /**
     * Checks by default if given property's value is of correct Java class.
     * Can be overridden by concrete classes to do extensive validation.
     *
     * @param value the value to check the validity of
     * @throws InvalidValueTypeException
     */
    public void checkValidity( final Value value )
        throws InvalidJavaTypeException, InvalidValueException
    {
        checkValueIsOfExpectedJavaClass( value );
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
    public String toString()
    {
        return name;
    }

    public boolean isValueOfExpectedJavaClass( final Object value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return javaType.isInstance( value );
    }

    public boolean isValueOfExpectedJavaClass( final Value value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return javaType.isInstance( value.getObject() );
    }

    void checkValueIsOfExpectedJavaClass( final Property property )
        throws InvalidValueTypeException
    {
        if ( !isValueOfExpectedJavaClass( ( property.getObject() ) ) )
        {
            throw new InvalidValueTypeException( javaType, property );
        }
    }

    void checkValueIsOfExpectedJavaClass( final Value value )
        throws InvalidJavaTypeException
    {
        if ( !isValueOfExpectedJavaClass( value ) )
        {
            throw new InvalidJavaTypeException( javaType, value );
        }
    }

    public Property newProperty( final String name, final Object valueObj )
    {
        final Value value = newValue( valueObj );
        return newProperty( name, value );
    }

    public abstract Value newValue( Object value );

    public abstract Property newProperty( final String name, final Value value );
}
