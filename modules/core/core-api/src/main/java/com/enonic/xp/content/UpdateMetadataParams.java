package com.enonic.xp.content;

import java.util.Locale;
import java.util.Objects;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class UpdateMetadataParams
{
    private final ContentId id;

    private final Locale language;

    private final PrincipalKey owner;

    private final Branches branches;

    private UpdateMetadataParams( final Builder builder )
    {
        this.id = builder.id;
        this.language = builder.language;
        this.owner = builder.owner;
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

    public Locale getLanguage()
    {
        return language;
    }

    public PrincipalKey getOwner()
    {
        return owner;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

        private ContentId id;

        private Locale language;

        private PrincipalKey owner;

        private Builder()
        {
        }

        public Builder contentId( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public Builder language( final Locale language )
        {
            this.language = language;
            return this;
        }

        public Builder owner( final PrincipalKey owner )
        {
            this.owner = owner;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches.addAll( branches );
            return this;
        }

        public UpdateMetadataParams build()
        {
            Objects.requireNonNull( this.id, "ContentId is required" );
            return new UpdateMetadataParams( this );
        }
    }
}
