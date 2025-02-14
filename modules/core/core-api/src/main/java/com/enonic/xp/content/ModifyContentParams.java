package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

@PublicApi
public final class ModifyContentParams
{
    private final ContentId id;

    private final ContentSuperEditor editor;

    private final CreateAttachments createAttachments;
//
//    private BinaryReferences removeAttachments = BinaryReferences.empty();
//
//    private boolean clearAttachments;

    private final Branches branches;

    private ModifyContentParams( final Builder builder )
    {
        this.id = builder.id;
        this.editor = builder.editor;
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

    public ContentSuperEditor getEditor()
    {
        return editor;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

        private ContentId id;

        private ContentSuperEditor editor;

        private CreateAttachments createAttachments = CreateAttachments.empty();

        private Builder()
        {
        }

        public Builder contentId( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public Builder editor( final ContentSuperEditor editor )
        {
            this.editor = editor;
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

        public ModifyContentParams build()
        {
            return new ModifyContentParams( this );
        }
    }
}
