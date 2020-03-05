package com.enonic.xp.project;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Projects
    extends AbstractImmutableEntitySet<Project>
{
    private Projects( final Builder builder )
    {
        super( builder.projects.build() );
    }

    public static Projects empty()
    {
        return create().build();
    }

    public static Projects from( Repositories repositories )
    {
        if ( repositories == null )
        {
            return null;
        }

        return create().addAll( repositories.stream().
            map( Project::from ).
            filter( Objects::nonNull ).
            collect( Collectors.toSet() ) ).
            build();
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
        private ImmutableSet.Builder<Project> projects = ImmutableSet.builder();

        public Builder addAll( Collection<Project> projects )
        {
            this.projects.addAll( projects );
            return this;
        }

        public Projects build()
        {
            return new Projects( this );
        }
    }
}
