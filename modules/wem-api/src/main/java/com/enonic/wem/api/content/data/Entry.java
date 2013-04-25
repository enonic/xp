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

    Entry( final String name )
    {
        EntryPath.Element.checkName( name );
        this.name = name;
    }

    /**
     * Creates a root Entry.
     */
    Entry()
    {
        this.name = "";
    }

    void setParent( final DataSet parent )
    {
        this.parent = parent;
    }

    public DataSet getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    EntryId getEntryId()
    {
        return EntryId.from( name, getArrayIndex() );
    }

    public EntryPath getPath()
    {
        if ( this.path == null )
        {
            this.path = resolvePath();
        }
        return this.path;
    }

    private EntryPath resolvePath()
    {
        if ( parent == null && StringUtils.isEmpty( this.name ) )
        {
            return EntryPath.ROOT;
        }

        final EntryPath.Element pathElement;
        final int arrayIndex = getArrayIndex();
        if ( arrayIndex > -1 && isArray() )
        {
            pathElement = EntryPath.Element.from( this.name, arrayIndex );
        }
        else
        {
            pathElement = EntryPath.Element.from( this.name );
        }

        final EntryPath newPath;
        if ( parent != null )
        {
            final EntryPath parentPath = parent.getPath();
            newPath = EntryPath.from( parentPath, pathElement );
        }
        else
        {
            newPath = EntryPath.from( pathElement );
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
                "This Entry at path [" + getPath().toString() + "] is not a Property: " + this.getClass().getSimpleName() );
        }
        return (Property) this;
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

    public boolean isArray()
    {
        return parent != null && parent.isArray( this );
    }

    public int getArrayIndex()
    {
        if ( parent == null )
        {
            return -1;
        }
        return parent.getArrayIndex( this );
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

