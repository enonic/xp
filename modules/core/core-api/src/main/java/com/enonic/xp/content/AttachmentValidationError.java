package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.util.BinaryReference;

public class AttachmentValidationError extends ValidationError
{
    private final BinaryReference binaryReference;

    public AttachmentValidationError( final BinaryReference binaryReference, final String errorCode, final String errorMessage, final Object... args )
    {
        super(errorCode, errorMessage, args);
        this.binaryReference = Objects.requireNonNull( binaryReference );
    }

    public BinaryReference getBinaryReference()
    {
        return binaryReference;
    }
}
