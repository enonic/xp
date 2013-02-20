package com.enonic.wem.api.content.datatype;


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

    @Override
    public String getIndexableString( final Object value )
    {
        throw new RuntimeException( "Not implemented method getIndexableString for " + this );
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
        checkCorrectType( data );
    }

    public void ensureType( final Data data )
        throws InconvertibleValueException
    {
        if ( data == null )
        {
            return;
        }

        data.setValue( ensureTypeOfValue( data.getValue() ) );
    }

    /**
     * Ensure that given value is of this type. If it is, it returns same value.
     * Subclasses, overriding this method should convert the given value when possible.
     * This method will not try to convert the given value, but throw an InconvertibleException
     * when given value is not this type.
     *
     * @param value
     */
    protected Value ensureTypeOfValue( final Value value )
        throws InconvertibleValueException
    {
        return value;
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

    public boolean hasCorrectType( Object value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return javaType.isInstance( value );
    }

    public boolean hasCorrectType( Value value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return javaType.isInstance( value.getObject() );
    }

    Value newValue( Object value )
    {
        return Value.newValue().type( this ).value( value ).build();
    }

    private void checkCorrectType( Data data )
        throws InvalidValueTypeException
    {
        if ( !hasCorrectType( ( data.getObject() ) ) )
        {
            throw new InvalidValueTypeException( javaType, data );
        }
    }

}
