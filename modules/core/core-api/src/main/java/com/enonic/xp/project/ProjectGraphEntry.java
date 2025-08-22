package com.enonic.xp.project;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ProjectGraphEntry
{
    private final ProjectName name;

    private final List<ProjectName> parents;

    private ProjectGraphEntry( Builder builder )
    {
        this.name = builder.name;
        this.parents = builder.parents.build();
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
        return !parents.isEmpty() ? parents.get( 0 ) : null;
    }

    public List<ProjectName> getParents()
    {
        return parents;
    }

    @Override
    public String toString()
    {
        return "ProjectGraphEntry{" + "name=" + name + ", parents=[" +
            parents.stream().map( ProjectName::toString ).collect( Collectors.joining( "," ) ) + "]}";
    }

    public static final class Builder
    {
        private ProjectName name;

        private final ImmutableList.Builder<ProjectName> parents = ImmutableList.builder();

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
            this.parents.add( parent );
            return this;
        }

        public Builder addParents( final Collection<ProjectName> parents )
        {
            this.parents.addAll( parents );
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( name, "name is required" );
        }


        public ProjectGraphEntry build()
        {
            validate();
            return new ProjectGraphEntry( this );
        }
    }
}
