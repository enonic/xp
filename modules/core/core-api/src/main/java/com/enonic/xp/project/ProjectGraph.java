package com.enonic.xp.project;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ProjectGraph
    extends AbstractImmutableEntityList<ProjectGraphEntry>
{
    private ProjectGraph( final Builder builder )
    {
        super( builder.projects.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<ProjectGraphEntry> projects = ImmutableList.builder();

        public Builder add( ProjectGraphEntry project )
        {
            this.projects.add( project );
            return this;
        }

        public ProjectGraph build()
        {
            return new ProjectGraph( this );
        }
    }
}
