package com.enonic.xp.content;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReferences;

@PublicApi
public class UpdateContentTranslatorParams
{
    private final Content editedContent;

    private final PrincipalKey modifier;

    private final Instant modifiedTime = Instant.now();

    private final CreateAttachments createAttachments;

    private final BinaryReferences removeAttachments;

    private final boolean clearAttachments;

    private UpdateContentTranslatorParams( final Builder builder )
    {
        editedContent = builder.editedContent;
        modifier = builder.modifier;
        createAttachments = builder.createAttachments;
        removeAttachments = builder.removeAttachments;
        clearAttachments = builder.clearAttachments;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content getEditedContent()
    {
        return editedContent;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
    }

    public BinaryReferences getRemoveAttachments()
    {
        return removeAttachments;
    }

    public boolean isClearAttachments()
    {
        return clearAttachments;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public static final class Builder
    {
        private Content editedContent;

        private PrincipalKey modifier;

        private CreateAttachments createAttachments = null;

        private BinaryReferences removeAttachments = null;

        private boolean clearAttachments = false;

        private Builder()
        {
        }

        public Builder editedContent( final Content editedContent )
        {
            this.editedContent = editedContent;
            return this;
        }

        public Builder modifier( final PrincipalKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder createAttachments( final CreateAttachments createAttachments )
        {
            this.createAttachments = createAttachments;
            return this;
        }

        public Builder removeAttachments( final BinaryReferences removeAttachments )
        {
            this.removeAttachments = removeAttachments;
            return this;
        }

        public Builder clearAttachments( final boolean clearAttachments )
        {
            this.clearAttachments = clearAttachments;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( modifier, "modifier cannot be null" );
            Preconditions.checkNotNull( editedContent, "contentId cannot be null" );
        }

        public UpdateContentTranslatorParams build()
        {
            validate();
            return new UpdateContentTranslatorParams( this );
        }
    }
}
