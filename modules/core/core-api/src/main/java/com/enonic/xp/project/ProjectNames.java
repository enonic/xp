package com.enonic.xp.project;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class ProjectNames
    extends AbstractImmutableEntitySet<ProjectName>
    implements Iterable<ProjectName>
{
    private ProjectNames( final Builder builder )
    {
        super( builder.projectNames.build() );
    }

    public static ProjectNames empty()
    {
        return create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableSet.Builder<ProjectName> projectNames = ImmutableSet.builder();

        public Builder add( final ProjectName projectName )
        {
            this.projectNames.add( projectName );
            return this;
        }

        public Builder addAll( final ProjectNames projectNames )
        {
            this.projectNames.addAll( projectNames.getSet() );
            return this;
        }


        public ProjectNames build()
        {
            return new ProjectNames( this );
        }
    }
}
