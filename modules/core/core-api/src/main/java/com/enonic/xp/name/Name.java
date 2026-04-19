package com.enonic.xp.name;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.core.internal.NameValidator;

import static java.util.Objects.requireNonNull;


@NullMarked
public abstract class Name
{
    protected final String value;

    protected Name( final String name )
    {
        this( name, true );
    }

    protected Name( final String name, final boolean validate )
    {
        this.value = requireNonNull( validate ? NameValidator.NAME.cachedExtend( getClass() ).validate( name ) : name );
    }

    @Override
    public final boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Name name = (Name) o;

        return value.equals( name.value );
    }

    @Override
    public final int hashCode()
    {
        return value.hashCode();
    }

    @Override
    public final String toString()
    {
        return value;
    }
}
