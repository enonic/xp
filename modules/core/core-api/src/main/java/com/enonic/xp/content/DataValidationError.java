package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.data.PropertyPath;

public final class DataValidationError
    extends ValidationError
{
    private final PropertyPath path;

    public DataValidationError( final PropertyPath path, final String errorCode, final String errorMessage, final Object... args )
    {
        super( errorCode, errorMessage, args );
        this.path = Objects.requireNonNull( path );
    }

    public PropertyPath getPath()
    {
        return path;
    }
}
