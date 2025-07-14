package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

@PublicApi
public final class PatchContentParams
{
    private final ContentId id;

    private final ContentPatcher patcher;

    private final CreateAttachments createAttachments;

    private final Branches branches;

    private PatchContentParams( final Builder builder )
    {
        this.id = builder.id;
        this.patcher = builder.patcher;
        this.createAttachments = builder.createAttachments;
        this.branches = Branches.from( builder.branches.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return id;
    }

    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
    }

    public ContentPatcher getPatcher()
    {
        return patcher;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

        private ContentId id;

        private ContentPatcher patcher;

        private CreateAttachments createAttachments = CreateAttachments.empty();

        private Builder()
        {
        }

        public Builder contentId( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public Builder patcher( final ContentPatcher patcher )
        {
            this.patcher = patcher;
            return this;
        }

        public Builder createAttachments( final CreateAttachments value )
        {
            this.createAttachments = Objects.requireNonNullElseGet( value, CreateAttachments::empty );
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches.addAll( branches );
            return this;
        }

        public PatchContentParams build()
        {
            return new PatchContentParams( this );
        }
    }
}
