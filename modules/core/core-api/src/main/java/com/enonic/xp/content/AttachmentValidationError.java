package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.util.BinaryReference;

public final class AttachmentValidationError
    extends ValidationError
{
    private final BinaryReference attachment;

    AttachmentValidationError( final BinaryReference attachment, final String errorCode, final String message, final String i18n,
                                      final Object[] args )
    {
        super( errorCode, message, i18n, args );
        this.attachment = Objects.requireNonNull( attachment );
    }

    public BinaryReference getAttachment()
    {
        return attachment;
    }
}
