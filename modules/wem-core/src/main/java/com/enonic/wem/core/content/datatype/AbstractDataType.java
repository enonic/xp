package com.enonic.wem.core.content.datatype;


import com.google.common.base.Objects;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;

public abstract class AbstractDataType
    implements DataType
{
    private final int key;

    private final String name;

    private JavaType javaType;

    private FieldType defaultFieldType;

    public AbstractDataType( int key, JavaType javaType, FieldType defaultFieldType )
    {
        this.key = key;
        this.name = this.getClass().getName();
        this.javaType = javaType;
        this.defaultFieldType = defaultFieldType;
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
    public FieldType getDefaultFieldType()
    {
        return defaultFieldType;
    }

    @Override
    public JavaType getJavaType()
    {
        return this.javaType;
    }

    @Override
    public String getIndexableString( final Object value )
    {
        throw new RuntimeException( "Not implemented method getIndexableString for " + this );
    }

    @Override
    public String convertToString( final Object value )
    {
        throw new RuntimeException( "Not implemented method convertToString for " + this );
    }

    @Override
    public boolean isConvertibleTo( final JavaType date )
    {
        throw new RuntimeException( "Not implemented method isConvertibleTo for " + this );
    }

    /**
     * Checks by default if given value is of correct Java class.
     * Can be overridden by concrete classes to do extensive validation.
     *
     * @param value
     * @throws InvalidValueTypeException
     */
    @Override
    public void checkValidity( final Object value )
        throws InvalidValueTypeException
    {
        checkCorrectType( value );
    }

    @Override
    public Object ensureType( final Object value )
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        else
        {
            throw new InconvertibleException( value, this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AbstractDataType ) )
        {
            return false;
        }

        final AbstractDataType that = (AbstractDataType) o;
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
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "key", key );
        s.add( "name", name );
        s.add( "javaType", javaType );
        s.add( "defaultFieldType", defaultFieldType );
        return s.toString();
    }

    boolean hasCorrectType( Object value )
    {
        return javaType.isInstance( value );
    }

    void checkCorrectType( Object value )
    {
        if ( !javaType.isInstance( value ) )
        {
            throw new InvalidValueTypeException( javaType, value );
        }
    }
}
