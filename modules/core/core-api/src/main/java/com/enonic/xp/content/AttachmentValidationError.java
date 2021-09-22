package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.util.BinaryReference;

public final class AttachmentValidationError
    extends ValidationError
{
    private final BinaryReference attachment;

    public AttachmentValidationError( final BinaryReference attachment, final String errorCode, final String message )
    {
        this( attachment, errorCode, message, null );
    }

    public AttachmentValidationError( final BinaryReference attachment, final String errorCode, final String message, final String i18n,
                                      final Object... args )
    {
        super( errorCode, message, i18n, args );
        this.attachment = Objects.requireNonNull( attachment );
    }

    public AttachmentValidationError( final BinaryReference attachment, final String errorCode, final String i18n,
                                      final Object... args )
    {

        this.attachment = Objects.requireNonNull( attachment );
    }

    public BinaryReference getAttachment()
    {
        return attachment;
    }
}
