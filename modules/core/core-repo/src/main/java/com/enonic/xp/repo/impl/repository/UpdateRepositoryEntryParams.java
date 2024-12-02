package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.repository.RepositoryId;

public final class UpdateRepositoryEntryParams
{
    private final RepositoryId repositoryId;

    private final PropertyTree repositoryData;

    private final BinaryAttachments attachments;

    private final Boolean transientFlag;

    private UpdateRepositoryEntryParams( Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.repositoryData = builder.repositoryData;
        this.attachments = builder.attachments.build();
        this.transientFlag = builder.transientFlag;
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

    public Boolean getTransientFlag()
    {
        return transientFlag;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RepositoryId repositoryId;

        private PropertyTree repositoryData;

        private final BinaryAttachments.Builder attachments = BinaryAttachments.create();

        private Boolean transientFlag;

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

        public Builder attachments( Iterable<BinaryAttachment> attachments )
        {
            if ( attachments != null )
            {
                attachments.forEach( this.attachments::add );
            }
            return this;
        }

        public Builder transientFlag( final Boolean value )
        {
            this.transientFlag = value;
            return this;
        }

        public UpdateRepositoryEntryParams build()
        {
            return new UpdateRepositoryEntryParams( this );
        }
    }
}
