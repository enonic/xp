package com.enonic.xp.repository;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;

@PublicApi
public final class Repository
{
    private final RepositoryId id;

    private final Branches branches;

    private final PropertyTree data;

    private final AttachedBinaries attachments;

    private final boolean transientFlag;

    private Repository( Builder builder )
    {
        this.id = builder.id;
        this.branches = builder.branches;
        this.data = Objects.requireNonNullElseGet( builder.data, PropertyTree::new );
        this.attachments = Objects.requireNonNullElseGet( builder.attachments, AttachedBinaries::empty );
        this.transientFlag = builder.transientFlag;
    }

    public RepositoryId getId()
    {
        return id;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public AttachedBinaries getAttachments()
    {
        return attachments;
    }

    public boolean isTransient()
    {
        return transientFlag;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId id;

        private Branches branches;

        private PropertyTree data;

        private AttachedBinaries attachments;

        private boolean transientFlag;

        private Builder()
        {
        }

        public Builder id( final RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }


        public Builder branches( final Branch... branches )
        {
            this.branches = Branches.from( branches );
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder transientFlag( final boolean value )
        {
            this.transientFlag = value;
            return this;
        }

        public Builder attachments( final AttachedBinaries attachments )
        {
            this.attachments = attachments;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( branches, "branches cannot be null" );
            Preconditions.checkArgument( branches.contains( RepositoryConstants.MASTER_BRANCH ), "branches must contain master branch" );
        }


        public Repository build()
        {
            validate();
            return new Repository( this );
        }
    }
}
