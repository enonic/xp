package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Version;

import static java.util.Objects.requireNonNullElseGet;

public final class RepositoryEntry
{
    private final RepositoryId id;

    private final RepositorySettings settings;

    private final PropertyTree data;

    private final AttachedBinaries attachments;

    private final boolean transientFlag;

    private final Version modelVersion;

    private RepositoryEntry( RepositoryEntry.Builder builder )
    {
        this.id = builder.id;
        this.settings = builder.settings == null ? RepositorySettings.create().build() : builder.settings;
        this.data = requireNonNullElseGet( builder.data, PropertyTree::new );
        this.attachments = requireNonNullElseGet( builder.attachments, AttachedBinaries::empty );
        this.transientFlag = builder.transientFlag;
        this.modelVersion = builder.modelVersion;
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

    public Version getModelVersion()
    {
        return modelVersion;
    }

    public static RepositoryEntry.Builder create()
    {
        return new RepositoryEntry.Builder();
    }

    public static RepositoryEntry.Builder create( final RepositoryEntry source )
    {
        return new RepositoryEntry.Builder( source );
    }

    public static final class Builder
    {
        private RepositoryId id;

        private RepositorySettings settings;

        private PropertyTree data;

        private AttachedBinaries attachments;

        private boolean transientFlag;

        private Version modelVersion;

        private Builder()
        {
        }

        private Builder( final RepositoryEntry source )
        {
            this.id = source.id;
            this.settings = source.settings;
            this.data = source.data;
            this.attachments = source.attachments;
            this.transientFlag = source.transientFlag;
            this.modelVersion = source.modelVersion;
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

        public RepositoryEntry.Builder modelVersion( final Version modelVersion )
        {
            this.modelVersion = modelVersion;
            return this;
        }

        public RepositoryEntry build()
        {
            return new RepositoryEntry( this );
        }
    }
}
