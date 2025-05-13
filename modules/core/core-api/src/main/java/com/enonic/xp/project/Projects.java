package com.enonic.xp.project;

import java.util.Collection;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Projects
    extends AbstractImmutableEntityList<Project>
{
    private Projects( final ImmutableList<Project> projects )
    {
        super( projects );
    }

    public static Projects empty()
    {
        return new Projects( ImmutableList.of() );
    }

    public static Projects from( Collection<Project> projects )
    {
        return new Projects( ImmutableList.copyOf( projects ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        for ( final Project project : this )
        {
            s.add( "project", project.toString() );
        }

        return s.toString();
    }

    public static class Builder
    {
        private final ImmutableList.Builder<Project> projects = ImmutableList.builder();

        public Builder addAll( Collection<Project> projects )
        {
            this.projects.addAll( projects );
            return this;
        }

        public Projects build()
        {
            return new Projects( projects.build() );
        }
    }
}
