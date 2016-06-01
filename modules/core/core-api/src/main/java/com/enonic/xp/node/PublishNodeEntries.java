package com.enonic.xp.node;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public class PublishNodeEntries
    extends AbstractImmutableEntitySet<PublishNodeEntry>
{
    private Branch targetBranch;

    private RepositoryId targetRepo;

    private PublishNodeEntries( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.entries ) );
        targetBranch = builder.targetBranch;
        targetRepo = builder.targetRepo;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeIds getNodeIds()
    {
        return NodeIds.from( this.set.stream().
            map( ( entry ) -> entry.getNodeBranchEntry().getNodeId() ).
            collect( Collectors.toSet() ) );
    }

    public Branch getTargetBranch()
    {
        return targetBranch;
    }

    public RepositoryId getTargetRepo()
    {
        return targetRepo;
    }

    public static final class Builder
    {
        private Set<PublishNodeEntry> entries = Sets.newHashSet();

        private Branch targetBranch;

        private RepositoryId targetRepo;

        private Builder()
        {
        }

        public Builder add( final PublishNodeEntry val )
        {
            this.entries.add( val );
            return this;
        }

        public Builder addAll( final Collection<PublishNodeEntry> values )
        {
            this.entries.addAll( values );
            return this;
        }


        public PublishNodeEntries build()
        {
            return new PublishNodeEntries( this );
        }

        public Builder targetBranch( final Branch val )
        {
            targetBranch = val;
            return this;
        }

        public Builder targetRepo( final RepositoryId val )
        {
            targetRepo = val;
            return this;
        }
    }
}
