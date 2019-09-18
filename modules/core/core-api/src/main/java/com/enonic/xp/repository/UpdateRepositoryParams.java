package com.enonic.xp.repository;

import java.util.Optional;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.BinaryAttachments;

public final class UpdateRepositoryParams
{
    private final RepositoryId repositoryId;

    private final RepositoryData data;

    private final BinaryAttachments attachments;

    private UpdateRepositoryParams( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.data = Optional.ofNullable( builder.data ).orElse( RepositoryData.empty() );
        this.attachments = Optional.ofNullable( builder.attachments ).orElse( BinaryAttachments.empty() );
    }

    public RepositoryData getData()
    {
        return data;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public BinaryAttachments getAttachments()
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

        private BinaryAttachments attachments;

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

        public Builder attachments( final BinaryAttachments attachments )
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
