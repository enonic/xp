package com.enonic.wem.api.content.datatype;


import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;
import com.enonic.wem.api.content.type.formitem.comptype.TypedPath;

public abstract class BaseDataType
    implements DataType
{
    private final int key;

    private final String name;

    private JavaType.BaseType javaType;

    /**
     * Only used when javaType is DataSet.
     */
    private Map<String, TypedPath> typedPaths;

    public BaseDataType( int key, JavaType.BaseType javaType, final TypedPath... typedPaths )
    {
        this.key = key;
        this.name = this.getClass().getSimpleName();
        this.javaType = javaType;

        if ( typedPaths != null && typedPaths.length > 0 )
        {
            Preconditions.checkArgument( this.javaType.equals( JavaType.DATA_SET ),
                                         "Specifying DataTypes for paths is only needed when javaType is " + JavaType.DATA_SET + ": " +
                                             this.javaType );
        }

        if ( this.javaType.equals( JavaType.DATA_SET ) && typedPaths != null )
        {
            this.typedPaths = new LinkedHashMap<String, TypedPath>();
            for ( TypedPath tp : typedPaths )
            {
                this.typedPaths.put( tp.getPath(), tp );
            }
        }
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

    public final void ensureType( final Data data )
        throws InconvertibleValueException
    {
        if ( data == null )
        {
            return;
        }

        if ( data.getValue() != null )
        {
            Object value = data.getValue();

            if ( javaType.isInstance( value ) )
            {
                if ( value instanceof com.enonic.wem.api.content.data.DataSet )
                {
                    final DataSet valueAsDataSet = (DataSet) value;
                    for ( TypedPath typedPath : typedPaths.values() )
                    {
                        final Data subData = valueAsDataSet.getData( typedPath.getPath() );
                        if ( subData == null )
                        {
                            throw new IllegalArgumentException(
                                "Missing data at path [" + valueAsDataSet.getPath().toString() + "." + typedPath.getPath() + "]" );
                        }
                        subData.setValue( typedPath.getDataType().ensureTypeOfValue( subData.getValue() ) );
                    }
                }
            }
            else
            {
                data.setValue( ensureTypeOfValue( data.getValue() ) );
            }
        }
    }

    /**
     * Ensure that given value is of this type. If it is, it returns same value.
     * Subclasses, overriding this method should convert the given value when possible.
     * This method will not try to convert the given value, but throw an InconvertibleException
     * when given value is not this type.
     */
    public Object ensureTypeOfValue( final Object value )
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
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "key", key );
        s.add( "name", name );
        s.add( "javaType", javaType );
        return s.toString();
    }

    boolean hasCorrectType( Object value )
    {
        Preconditions.checkNotNull( value, "Cannot check the type of a value that is null" );
        return javaType.isInstance( value );
    }

    private void checkCorrectType( Data data )
        throws InvalidValueTypeException
    {
        if ( !data.hasValue() )
        {
            return;
        }

        if ( this.javaType == JavaType.DATA_SET && typedPaths.size() > 0 )
        {
            final DataSet dataSet = data.getDataSet();
            for ( TypedPath typedPath : typedPaths.values() )
            {
                final Data subData = dataSet.getData( typedPath.getPath() );
                if ( subData != null )
                {
                    typedPath.getDataType().checkCorrectType( subData );
                }
            }
        }
        else if ( !hasCorrectType( ( data.getValue() ) ) )
        {
            throw new InvalidValueTypeException( javaType, data );
        }
    }

}
