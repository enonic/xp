package com.enonic.xp.core.content;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.content.attachment.CreateAttachments;
import com.enonic.xp.core.security.PrincipalKey;

public class UpdateContentTranslatorParams
{
    private final Content editedContent;

    private final PrincipalKey modifier;

    private final Instant modifiedTime = Instant.now();

    private final CreateAttachments createAttachments;

    private UpdateContentTranslatorParams( final Builder builder )
    {
        editedContent = builder.editedContent;
        modifier = builder.modifier;
        createAttachments = builder.createAttachments;
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

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public static final class Builder
    {
        private Content editedContent;

        private PrincipalKey modifier;

        private CreateAttachments createAttachments = null;

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
