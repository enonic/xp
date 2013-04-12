package com.enonic.wem.api.content.data.type;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public abstract class BaseDataType
    implements DataType
{
    private final int key;

    private final String name;

    private JavaType.BaseType javaType;

    public BaseDataType( int key, JavaType.BaseType javaType )
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
     * Checks by default if given data's value is of correct Java class.
     * Can be overridden by concrete classes to do extensive validation.
     *
     * @param data the data to check the validity of
     * @throws InvalidValueTypeException
     */
    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        checkValueIsOfExpectedJavaClass( data );
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BaseDataType ) )
        {
            return false;
        }

        final BaseDataType that = (BaseDataType) o;
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

    void checkValueIsOfExpectedJavaClass( Data data )
        throws InvalidValueTypeException
    {
        if ( !isValueOfExpectedJavaClass( ( data.getObject() ) ) )
        {
            throw new InvalidValueTypeException( javaType, data );
        }
    }

    public abstract Value newValue( Object value );

    public abstract Value.AbstractValueBuilder newValueBuilder();

    public abstract Data newData( final String name, final Value value );

    public Data newData( final String name, final Object valueObj )
    {
        final Value value = newValue( valueObj );
        return newData( name, value );
    }
}
