package com.enonic.xp.repo.impl.repository;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.repository.RepositoryId;

public final class UpdateRepositoryEntryParams
{
    private final RepositoryId repositoryId;

    private final PropertyTree repositoryData;

    private final BinaryAttachments attachments;

    private UpdateRepositoryEntryParams( Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.repositoryData = builder.repositoryData;
        this.attachments = builder.attachments.build();
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public PropertyTree getRepositoryData()
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

        private PropertyTree repositoryData;

        private BinaryAttachments.Builder attachments = BinaryAttachments.create();

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
            if ( attachments != null )
            {
                attachments.forEach( this.attachments::add );
            }
            return this;
        }

        public UpdateRepositoryEntryParams build()
        {
            return new UpdateRepositoryEntryParams( this );
        }
    }
}
