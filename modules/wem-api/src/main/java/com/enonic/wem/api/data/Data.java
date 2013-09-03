package com.enonic.wem.api.data;


import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public abstract class Data
{
    protected static final String ROOT_NAME = "";

    private final String name;

    private int arrayIndex;

    /**
     * Null if this Data have no parent yet.
     */
    private DataSet parent;

    /**
     * Cached path to this Data.
     */
    private volatile DataPath path;

    Data( final Data source )
    {
        this.name = source.name;
        this.arrayIndex = source.arrayIndex;
    }

    Data( final String name )
    {
        if ( name != null && !name.equals( ROOT_NAME ) )
        {
            DataPath.Element.checkName( name );
        }
        this.name = name;
    }

    /**
     * Creates a root Data.
     */
    Data()
    {
        this.name = ROOT_NAME;
    }

    void setParent( final DataSet parent )
    {
        this.parent = parent;
    }

    void setArrayIndex( final int arrayIndex )
    {
        this.arrayIndex = arrayIndex;
    }

    public DataSet getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    DataId getDataId()
    {
        return DataId.from( name, arrayIndex );
    }

    public DataPath getPath()
    {
        if ( this.path == null )
        {
            this.path = resolvePath();
        }
        return this.path;
    }

    private DataPath resolvePath()
    {
        if ( parent == null && StringUtils.isEmpty( this.name ) )
        {
            return DataPath.ROOT;
        }

        final DataPath.Element pathElement;
        final int arrayIndex = getArrayIndex();
        if ( arrayIndex > -1 && isArray() )
        {
            pathElement = DataPath.Element.from( this.name, arrayIndex );
        }
        else
        {
            pathElement = DataPath.Element.from( this.name );
        }

        final DataPath newPath;
        if ( parent != null )
        {
            final DataPath parentPath = parent.getPath();
            newPath = DataPath.from( parentPath, pathElement );
        }
        else
        {
            newPath = DataPath.from( pathElement );
        }

        return newPath;
    }

    void invalidatePath()
    {
        this.path = null;
    }

    public boolean isProperty()
    {
        return this instanceof Property;
    }

    public Property toProperty()
    {
        if ( !( this instanceof Property ) )
        {
            throw new IllegalArgumentException(
                "This Data at path [" + getPath().toString() + "] is not a Property: " + this.getClass().getSimpleName() );
        }
        return (Property) this;
    }

    public boolean isDataSet()
    {
        return this instanceof DataSet;
    }

    public DataSet toDataSet()
    {
        Preconditions.checkArgument( isDataSet(), "This Data at path [%s] not a DataSet: " + this.getClass().getSimpleName(),
                                     getPath().toString() );
        return (DataSet) this;
    }

    public boolean isArray()
    {
        return parent != null && parent.isArray( this );
    }

    public int getArrayIndex()
    {
        return arrayIndex;
    }

    DataArray getArray()
    {
        if ( parent == null )
        {
            return null;
        }

        return parent.getArray( this );
    }

    public abstract Data copy();
}

