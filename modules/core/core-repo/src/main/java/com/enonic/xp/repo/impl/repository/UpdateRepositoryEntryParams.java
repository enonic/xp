package com.enonic.xp.repo.impl.repository;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryAttachment;

public final class UpdateRepositoryEntryParams
{
    private final RepositoryId repositoryId;

    private final PropertyTree repositoryData;

    private final ImmutableList<BinaryAttachment> attachments;

    private UpdateRepositoryEntryParams( Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.repositoryData = builder.repositoryData;
        this.attachments = builder.attachments;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public PropertyTree getRepositoryData()
    {
        return repositoryData;
    }

    public ImmutableList<BinaryAttachment> getAttachments()
    {
        return attachments;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RepositoryId repositoryId;

        private PropertyTree repositoryData;

        private ImmutableList<BinaryAttachment> attachments;

        public Builder repositoryId( RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder repositoryData( PropertyTree repositoryData )
        {
            this.repositoryData = repositoryData;
            return this;
        }

        public Builder attachments( ImmutableList<BinaryAttachment> attachments )
        {
            this.attachments = attachments;
            return this;
        }

        UpdateRepositoryEntryParams build()
        {
            return new UpdateRepositoryEntryParams( this );
        }
    }
}
