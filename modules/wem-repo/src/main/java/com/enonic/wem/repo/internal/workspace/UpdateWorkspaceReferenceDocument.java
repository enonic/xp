package com.enonic.wem.repo.internal.workspace;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class UpdateWorkspaceReferenceDocument
{
    private final ImmutableSet<String> workspaces;

    public ImmutableSet<String> workspaceNames()
    {
        return workspaces;
    }

    private UpdateWorkspaceReferenceDocument( final Builder builder )
    {
        this.workspaces = ImmutableSet.copyOf( builder.workspaceNames );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<String> workspaceNames = Sets.newHashSet();

        public Builder add( final String workspaceName )
        {
            this.workspaceNames.add( workspaceName );
            return this;
        }

        public Builder addAll( final Collection<Object> workspaceNames )
        {
            for ( final Object workspaceName : workspaceNames )
            {
                this.workspaceNames.add( workspaceName.toString() );
            }

            return this;
        }

        public Builder remove( final String workspace )
        {
            this.workspaceNames.remove( workspace );
            return this;
        }

        public UpdateWorkspaceReferenceDocument build()
        {
            return new UpdateWorkspaceReferenceDocument( this );
        }
    }


}
