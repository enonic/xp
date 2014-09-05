package com.enonic.wem.core.workspace.compare;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.NodeComparison;

public class WorkspaceComparison
{
    private final ImmutableSet<NodeComparison> diffEntries;

    private WorkspaceComparison( final Builder builder )
    {
        this.diffEntries = ImmutableSet.copyOf( builder.diffEntries );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSet<NodeComparison> getDiffEntries()
    {
        return diffEntries;
    }

    public static class Builder
    {
        private final Set<NodeComparison> diffEntries = Sets.newLinkedHashSet();

        private Builder()
        {
        }

        final Builder add( final NodeComparison diffEntry )
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
