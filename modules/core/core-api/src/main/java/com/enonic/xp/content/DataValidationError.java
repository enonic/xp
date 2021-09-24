package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.data.PropertyPath;

public final class DataValidationError
    extends ValidationError
{
    private final PropertyPath propertyPath;

    DataValidationError( final PropertyPath propertyPath, final String errorCode, final String message, final String i18n,
                         final Object[] args )
    {
        super( errorCode, message, i18n, args );
        this.propertyPath = Objects.requireNonNull( propertyPath );
    }

    public PropertyPath getPropertyPath()
    {
        return propertyPath;
    }
}
