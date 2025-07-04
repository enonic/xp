package com.enonic.xp.project;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ProjectGraph
    extends AbstractImmutableEntityList<ProjectGraphEntry>
{
    private ProjectGraph( final ImmutableList<ProjectGraphEntry> projects )
    {
        super( projects );
    }

    public static Collector<ProjectGraphEntry, ?, ProjectGraph> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ProjectGraph::new );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ProjectGraphEntry> projects = ImmutableList.builder();

        public Builder add( ProjectGraphEntry project )
        {
            this.projects.add( project );
            return this;
        }

        public ProjectGraph build()
        {
            return new ProjectGraph( this.projects.build() );
        }
    }
}
