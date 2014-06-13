package com.enonic.wem.core.workspace.diff;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class WorkspacesDifferences
{
    private final ImmutableSet<WorkspaceDiffEntry> diffEntries;

    private WorkspacesDifferences( final Builder builder )
    {
        this.diffEntries = ImmutableSet.copyOf( builder.diffEntries );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSet<WorkspaceDiffEntry> getDiffEntries()
    {
        return diffEntries;
    }

    public static class Builder
    {
        private final Set<WorkspaceDiffEntry> diffEntries = Sets.newLinkedHashSet();

        private Builder()
        {
        }

        final Builder add( final WorkspaceDiffEntry diffEntry )
        {
            this.diffEntries.add( diffEntry );
            return this;
        }

        final WorkspacesDifferences build()
        {
            return new WorkspacesDifferences( this );
        }

    }


}
