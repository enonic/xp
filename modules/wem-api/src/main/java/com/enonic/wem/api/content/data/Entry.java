package com.enonic.wem.api.content.data;


import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public abstract class Entry
{
    private final String name;

    /**
     * Null if this Entry have no parent yet.
     */
    private DataSet parent;

    /**
     * Cached path to this entry.
     */
    private volatile EntryPath path;

    Entry( final String name, final DataSet parent )
    {
        if ( name != null )
        {
            EntryPath.Element.checkName( name );
        }
        this.name = name;
        this.parent = parent;
    }

    Entry( final String name )
    {
        EntryPath.Element.checkName( name );
        this.name = name;
    }

    EntryId getEntryId()
    {
        return EntryId.from( name, getArrayIndex() );
    }

    public String getName()
    {
        return name;
    }

    void setParent( final DataSet entries )
    {
        this.parent = entries;
    }

    public DataSet getParent()
    {
        return parent;
    }

    public EntryPath getPath()
    {
        if ( this.path == null )
        {
            this.path = resolvePath();
        }
        return this.path;
    }

    public void invalidatePath()
    {
        this.path = null;
    }

    public int getArrayIndex()
    {
        if ( parent == null )
        {
            return -1;
        }
        return parent.getArrayIndex( this );
    }

    public boolean isData()
    {
        return this instanceof Data;
    }

    public Data toData()
    {
        if ( !( this instanceof Data ) )
        {
            throw new IllegalArgumentException(
                "This Entry at path [" + getPath().toString() + "] is not a Data: " + this.getClass().getSimpleName() );
        }
        return (Data) this;
    }

    public boolean isDataSet()
    {
        return this instanceof DataSet;
    }

    public DataSet toDataSet()
    {
        Preconditions.checkArgument( isDataSet(), "This Entry at path [%s] not a DataSet: " + this.getClass().getSimpleName(),
                                     getPath().toString() );
        return (DataSet) this;
    }

    private EntryPath resolvePath()
    {
        if ( parent == null && StringUtils.isEmpty( this.name ) )
        {
            return EntryPath.ROOT;
        }

        final EntryPath.Element name;
        final int arrayIndex = getArrayIndex();
        if ( arrayIndex > -1 && isArray() )
        {
            name = EntryPath.Element.from( this.name, arrayIndex );
        }
        else
        {
            name = EntryPath.Element.from( this.name );
        }

        final EntryPath newPath;
        if ( parent != null )
        {
            final EntryPath parentPath = parent.getPath();
            newPath = EntryPath.from( parentPath, name );
        }
        else
        {
            newPath = EntryPath.from( name );
        }

        return newPath;
    }

    public boolean isArray()
    {
        return parent != null && parent.isArray( this );
    }

    EntryArray getArray()
    {
        if ( parent == null )
        {
            return null;
        }

        return parent.getArray( this );
    }
}

