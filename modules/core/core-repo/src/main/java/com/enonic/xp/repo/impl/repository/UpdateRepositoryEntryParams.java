package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.repository.RepositoryData;
import com.enonic.xp.repository.RepositoryId;

public class UpdateRepositoryEntryParams
{
    private final RepositoryId repositoryId;
    private final RepositoryData repositoryData;
    private final BinaryAttachments attachments;

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

    public RepositoryData getRepositoryData()
    {
        return repositoryData;
    }

    public BinaryAttachments getAttachments()
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
        private RepositoryData repositoryData;
        private BinaryAttachments attachments;

        public Builder repositoryId( RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder repositoryData( RepositoryData repositoryData )
        {
            this.repositoryData = repositoryData;
            return this;
        }

        public Builder attachments( BinaryAttachments attachments )
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
