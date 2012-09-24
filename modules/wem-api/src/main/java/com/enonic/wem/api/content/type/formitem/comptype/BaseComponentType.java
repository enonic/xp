package com.enonic.wem.api.content.type.formitem.comptype;


import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

public abstract class BaseComponentType
    implements ComponentType
{
    private String className;

    private String name;

    private BaseDataType dataType;

    /**
     * Only used when dataType is DataSet.
     */
    private Map<String, TypedPath> typedPaths;

    BaseComponentType( final String name, final BaseDataType dataType, final TypedPath... typedPaths )
    {
        this.name = name;
        this.dataType = dataType;
        this.className = this.getClass().getName();

        if ( typedPaths != null && typedPaths.length > 0 )
        {
            Preconditions.checkArgument( this.dataType.equals( DataTypes.DATA_SET ),
                                         "Specifying DataTypes for paths is only needed when dataType is DataSet: " + this.dataType );
        }

        if ( this.dataType.equals( DataTypes.DATA_SET ) && typedPaths != null )
        {
            this.typedPaths = new LinkedHashMap<String, TypedPath>();
            for ( TypedPath tp : typedPaths )
            {
                this.typedPaths.put( tp.getPath(), tp );
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public String getClassName()
    {
        return className;
    }

    public DataType getDataType()
    {
        return dataType;
    }

    public AbstractComponentTypeConfigSerializerJson getComponentTypeConfigJsonGenerator()
    {
        return null;
    }

    @Override
    public AbstractComponentTypeConfigSerializerXml getComponentTypeConfigXmlGenerator()
    {
        return null;
    }

    @Override
    public void ensureType( final Data data )
    {
        if ( !dataType.equals( DataTypes.DATA_SET ) && data.getDataType().equals( dataType ) )
        {
            return;
        }

        if ( !dataType.equals( DataTypes.DATA_SET ) )
        {
            dataType.ensureType( data );
        }
        else if ( dataType.equals( DataTypes.DATA_SET ) )
        {
            final DataSet dataSet = data.getDataSet();

            for ( TypedPath typedPath : typedPaths.values() )
            {
                final Data dataAtPath = dataSet.getData( typedPath.getPath() );
                if ( dataAtPath == null )
                {
                    throw new IllegalArgumentException(
                        "Missing data at path [" + data.getPath().toString() + "." + typedPath.getPath() + "]" );
                }
                else
                {
                    typedPath.getDataType().ensureType( dataAtPath );
                }
            }
        }
    }

    public void checkValidity( Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        dataType.checkValidity( data );

        if ( typedPaths != null && data.hasDataSetAsValue() )
        {
            DataSet dataSet = data.getDataSet();
            for ( TypedPath typedPath : typedPaths.values() )
            {
                Data subData = dataSet.getData( typedPath.getPath() );
                if ( subData != null )
                {
                    typedPath.getDataType().checkValidity( subData );
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BaseComponentType ) )
        {
            return false;
        }

        final BaseComponentType that = (BaseComponentType) o;

        if ( !className.equals( that.className ) )
        {
            return false;
        }
        if ( !name.equals( that.name ) )
        {
            return false;
        }
        if ( !dataType.equals( that.dataType ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = className.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + dataType.hashCode();
        return result;
    }
}
