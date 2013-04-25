package com.enonic.wem.api.content.data.type;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public abstract class BasePropertyType
    implements PropertyType
{
    private final int key;

    private final String name;

    private JavaType.BaseType javaType;

    public BasePropertyType( int key, JavaType.BaseType javaType )
    {
        this.key = key;
        this.name = this.getClass().getSimpleName();
        this.javaType = javaType;
    }

    @Override
    public int getKey()
    {
        return key;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
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
    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        checkValueIsOfExpectedJavaClass( property );
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BasePropertyType ) )
        {
            return false;
        }

        final BasePropertyType that = (BasePropertyType) o;
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

    public boolean isValueOfExpectedJavaClass( Object value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return javaType.isInstance( value );
    }

    public boolean isValueOfExpectedJavaClass( Value value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return javaType.isInstance( value.getObject() );
    }

    void checkValueIsOfExpectedJavaClass( Property property )
        throws InvalidValueTypeException
    {
        if ( !isValueOfExpectedJavaClass( ( property.getObject() ) ) )
        {
            throw new InvalidValueTypeException( javaType, property );
        }
    }

    public abstract Value newValue( Object value );

    public abstract Value.AbstractValueBuilder newValueBuilder();

    public abstract Property newData( final String name, final Value value );

    public Property newData( final String name, final Object valueObj )
    {
        final Value value = newValue( valueObj );
        return newData( name, value );
    }
}
