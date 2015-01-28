package com.enonic.wem.api.workspace;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class Workspace
{
    private final String name;

    private Workspace( final Builder builder )
    {
        this.name = builder.name;
    }

    public static Workspace from( final String name )
    {
        return Workspace.create().
            name( name ).
            build();
    }

    public String getName()
    {
        return name;
    }


    public static Builder create()
    {
        return new Builder();
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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Workspace workspace = (Workspace) o;
        return name.equals( workspace.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public static final class Builder
    {
        private String name;

        private Builder()
        {
        }

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( this.name ) );
        }

        public Workspace build()
        {
            return new Workspace( this );
        }
    }
}


