package com.enonic.xp.repo.impl.repository;

import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.repository.RepositoryId;

public final class RepositoryEntry
{
    private final RepositoryId id;

    private final RepositorySettings settings;

    private final PropertyTree data;

    private final AttachedBinaries attachments;

    private final boolean transientFlag;

    private RepositoryEntry( RepositoryEntry.Builder builder )
    {
        this.id = builder.id;
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

    public static final class Builder
    {
        private RepositoryId id;

        private RepositorySettings settings;

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
