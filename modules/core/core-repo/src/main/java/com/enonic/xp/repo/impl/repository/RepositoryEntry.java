package com.enonic.xp.repo.impl.repository;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public final class RepositoryEntry
{
    private final RepositoryId id;

    private final Branches branches;

    private final RepositorySettings settings;

    private final PropertyTree data;

    private final AttachedBinaries attachments;

    private final boolean transientFlag;

    private RepositoryEntry( RepositoryEntry.Builder builder )
    {
        this.id = builder.id;
        this.branches = builder.branches;
        this.settings = builder.settings == null ? RepositorySettings.create().build() : builder.settings;
        this.data = Objects.requireNonNullElseGet( builder.data, PropertyTree::new );
        this.attachments = Objects.requireNonNullElseGet( builder.attachments, AttachedBinaries::empty );
        this.transientFlag = builder.transientFlag;
    }

    public RepositoryId getId()
    {
        return id;
    }

    public RepositorySettings getSettings()
    {
        return settings;
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

    public static RepositoryEntry.Builder create()
    {
        return new RepositoryEntry.Builder();
    }

    public Repository asRepository()
    {
        return Repository.create()
            .id( this.id )
            .branches( this.branches )
            .data( this.data.copy() )
            .attachments( this.attachments )
            .transientFlag( this.transientFlag )
            .build();
    }

    public static final class Builder
    {
        private RepositoryId id;

        private RepositorySettings settings;

        private Branches branches;

        private PropertyTree data;

        private AttachedBinaries attachments;

        private boolean transientFlag;

        private Builder()
        {
        }

        public RepositoryEntry.Builder id( final RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public RepositoryEntry.Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }


        public RepositoryEntry.Builder branches( final Branch... branches )
        {
            this.branches = Branches.from( branches );
            return this;
        }

        public RepositoryEntry.Builder settings( final RepositorySettings settings )
        {
            this.settings = settings;
            return this;
        }

        public RepositoryEntry.Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public RepositoryEntry.Builder transientFlag( final boolean value )
        {
            this.transientFlag = value;
            return this;
        }

        public RepositoryEntry.Builder attachments( final AttachedBinaries attachments )
        {
            this.attachments = attachments;
            return this;
        }

        public RepositoryEntry build()
        {
            return new RepositoryEntry( this );
        }
    }
}
