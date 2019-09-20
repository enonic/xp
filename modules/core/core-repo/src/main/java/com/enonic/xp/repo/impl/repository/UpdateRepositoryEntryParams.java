package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.repository.RepositoryBinaryAttachments;
import com.enonic.xp.repository.RepositoryData;
import com.enonic.xp.repository.RepositoryId;

public class UpdateRepositoryEntryParams
{
    private final RepositoryId repositoryId;
    private final RepositoryData repositoryData;
    private final RepositoryBinaryAttachments attachments;

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

    public RepositoryBinaryAttachments getAttachments()
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
        private RepositoryBinaryAttachments attachments;

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

        public Builder attachments( RepositoryBinaryAttachments attachments )
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
