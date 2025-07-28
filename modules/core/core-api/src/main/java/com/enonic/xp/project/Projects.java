package com.enonic.xp.project;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Projects
    extends AbstractImmutableEntityList<Project>
{
    private static final Projects EMPTY = new Projects( ImmutableList.of() );

    private Projects( final ImmutableList<Project> projects )
    {
        super( projects );
    }

    public static Projects empty()
    {
        return EMPTY;
    }

    public static Projects from( final Iterable<Project> projects )
    {
        return projects instanceof Projects p ? p : fromInternal( ImmutableList.copyOf( projects ) );
    }

    public static Collector<Project, ?, Projects> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Projects::fromInternal );
    }

    private static Projects fromInternal( final ImmutableList<Project> projects )
    {
        return projects.isEmpty() ? EMPTY : new Projects( projects );
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

    public static final class Builder
    {
        private final ImmutableList.Builder<Project> projects = ImmutableList.builder();


        public Builder add( final Project project )
        {
            projects.add( project );
            return this;
        }

        public Builder addAll( Iterable<Project> projects )
        {
            this.projects.addAll( projects );
            return this;
        }

        public Projects build()
        {
            return fromInternal( projects.build() );
        }
    }
}
