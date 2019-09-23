package com.enonic.xp.repository;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class RepositoryBinaryAttachments
    extends AbstractImmutableEntityList<RepositoryBinaryAttachment>
{
    private static final RepositoryBinaryAttachments EMPTY = new RepositoryBinaryAttachments( ImmutableList.of() );

    private RepositoryBinaryAttachments( final ImmutableList<RepositoryBinaryAttachment> set )
    {
        super( set );
    }

    public static RepositoryBinaryAttachments empty()
    {
        return EMPTY;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<RepositoryBinaryAttachment> binaryAttachmentsBuilder = ImmutableList.builder();

        public Builder add( final RepositoryBinaryAttachment attachedBinary )
        {
            binaryAttachmentsBuilder.add( attachedBinary );
            return this;
        }

        public RepositoryBinaryAttachments build()
        {
            return new RepositoryBinaryAttachments( binaryAttachmentsBuilder.build() );
        }
    }
}
