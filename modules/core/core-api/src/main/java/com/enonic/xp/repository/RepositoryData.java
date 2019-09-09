package com.enonic.xp.repository;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyTree;

@Beta
public class RepositoryData
{
    private final PropertyTree value;

    private RepositoryData( PropertyTree value )
    {
        this.value = value;
    }

    public PropertyTree getValue()
    {
        return value;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        RepositoryData that = (RepositoryData) o;
        return Objects.equals( value, that.value );
    }

    @Override
    public int hashCode() {
        return Objects.hash( value );
    }

    public static RepositoryData from( PropertyTree value ) {
        return value == null ? empty() : new RepositoryData( value.copy() );
    }

    public static RepositoryData empty() {
        return new RepositoryData( new PropertyTree() );
    }
}
