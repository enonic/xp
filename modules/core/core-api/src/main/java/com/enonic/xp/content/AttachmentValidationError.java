package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.util.BinaryReference;

public final class AttachmentValidationError
    extends ValidationError
{
    private final BinaryReference attachment;

    public AttachmentValidationError( final BinaryReference attachment, final String errorCode, final String errorMessage, final Object... args )
    {
        super( errorCode, errorMessage, args );
        this.attachment = Objects.requireNonNull( attachment );
    }

    public BinaryReference getAttachment()
    {
        return attachment;
    }
}
