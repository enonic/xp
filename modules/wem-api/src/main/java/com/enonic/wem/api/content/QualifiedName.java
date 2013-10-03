package com.enonic.wem.api.content;


import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public abstract class QualifiedName
{
    private final String name;

    protected QualifiedName( final String name )
    {
        Preconditions.checkNotNull( name, "QualifiedName is null" );
        Preconditions.checkArgument( StringUtils.isNotBlank( name ), "QualifiedName is blank" );

        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final QualifiedName that = (QualifiedName) o;

        return name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
