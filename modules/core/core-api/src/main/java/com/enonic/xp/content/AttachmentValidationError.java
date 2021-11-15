package com.enonic.xp.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.util.BinaryReference;

public final class AttachmentValidationError
    extends ValidationError
{
    private final BinaryReference attachment;

    AttachmentValidationError( final BinaryReference attachment, final ValidationErrorCode errorCode, final String message, final String i18n,
                                      final List<Object> args )
    {
        super( errorCode, message, i18n, args );
        this.attachment = Objects.requireNonNull( attachment );
    }

    public BinaryReference getAttachment()
    {
        return attachment;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final AttachmentValidationError that = (AttachmentValidationError) o;
        return attachment.equals( that.attachment );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), attachment );
    }
}
