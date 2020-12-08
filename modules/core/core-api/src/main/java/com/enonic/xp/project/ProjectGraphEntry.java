package com.enonic.xp.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ProjectGraphEntry
{
    private final ProjectName name;

    private final ProjectName parent;

    private ProjectGraphEntry( Builder builder )
    {
        this.name = builder.name;
        this.parent = builder.parent;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectName getName()
    {
        return name;
    }

    public ProjectName getParent()
    {
        return parent;
    }

    @Override
    public String toString()
    {
        return "ProjectGraphEntry{" + "name=" + name + ", parent=" + parent + '}';
    }

    public static final class Builder
    {
        private ProjectName name;

        private ProjectName parent;

        private Builder()
        {
        }

        public Builder name( final ProjectName value )
        {
            this.name = value;
            return this;
        }

        public Builder parent( final ProjectName parent )
        {
            this.parent = parent;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name cannot be null" );
        }


        public ProjectGraphEntry build()
        {
            validate();
            return new ProjectGraphEntry( this );
        }
    }
}
