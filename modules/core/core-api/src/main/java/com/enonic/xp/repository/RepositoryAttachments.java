package com.enonic.xp.repository;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;

@Beta
public class RepositoryAttachments
{
    private final BinaryAttachments binaryAttachments;

    private RepositoryAttachments( BinaryAttachments binaryAttachments )
    {
        this.binaryAttachments = binaryAttachments;
    }

    public BinaryAttachments getBinaryAttachments()
    {
        return binaryAttachments;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        RepositoryAttachments that = (RepositoryAttachments) o;
        return Objects.equals( binaryAttachments, that.binaryAttachments );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( binaryAttachments );
    }

    public static RepositoryAttachments from( BinaryAttachments attachments )
    {
        return new RepositoryAttachments( attachments );
    }

    public static RepositoryAttachments empty()
    {
        return new RepositoryAttachments( BinaryAttachments.empty() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private BinaryAttachments.Builder binaryAttachments = BinaryAttachments.create();

        public Builder addBinaryAttachment( BinaryAttachment binaryAttachment )
        {
            binaryAttachments.add( binaryAttachment );
            return this;
        }

        public RepositoryAttachments build()
        {
            return new RepositoryAttachments( binaryAttachments.build() );
        }
    }
}
