package com.enonic.wem.core.workspace.compare;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.EntityComparison;

public class WorkspaceComparison
{
    private final ImmutableSet<EntityComparison> diffEntries;

    private WorkspaceComparison( final Builder builder )
    {
        this.diffEntries = ImmutableSet.copyOf( builder.diffEntries );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSet<EntityComparison> getDiffEntries()
    {
        return diffEntries;
    }

    public static class Builder
    {
        private final Set<EntityComparison> diffEntries = Sets.newLinkedHashSet();

        private Builder()
        {
        }

        final Builder add( final EntityComparison diffEntry )
        {
            this.diffEntries.add( diffEntry );
            return this;
        }

        final WorkspaceComparison build()
        {
            return new WorkspaceComparison( this );
        }

    }


}
