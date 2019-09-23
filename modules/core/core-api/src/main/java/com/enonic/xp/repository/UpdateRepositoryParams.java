package com.enonic.xp.repository;

import java.util.Optional;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class UpdateRepositoryParams
{
    private final RepositoryId repositoryId;

    private final RepositoryData data;

    private final RepositoryBinaryAttachments attachments;

    private UpdateRepositoryParams( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.data = Optional.ofNullable( builder.data ).orElse( RepositoryData.empty() );
        this.attachments = Optional.ofNullable( builder.attachments ).orElse( RepositoryBinaryAttachments.empty() );
    }

    public RepositoryData getData()
    {
        return data;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public RepositoryBinaryAttachments getAttachments()
    {
        return attachments;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;
        private RepositoryData data;

        private RepositoryBinaryAttachments attachments;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder data( final RepositoryData data )
        {
            this.data = data;
            return this;
        }

        public Builder attachments( final RepositoryBinaryAttachments attachments )
        {
            this.attachments = attachments;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( repositoryId, "repositoryId cannot be null" );
        }


        public UpdateRepositoryParams build()
        {
            validate();
            return new UpdateRepositoryParams( this );
        }
    }
}
