package com.enonic.xp.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.data.PropertyPath;

public sealed class DataValidationError
    extends ValidationError
    permits SiteConfigValidationError, MixinConfigValidationError, ComponentConfigValidationError
{
    private final PropertyPath propertyPath;

    DataValidationError( final PropertyPath propertyPath, final ValidationErrorCode errorCode, final String message, final String i18n,
                         final List<Object> args )
    {
        super( errorCode, message, i18n, args );
        this.propertyPath = Objects.requireNonNull( propertyPath );
    }

    public PropertyPath getPropertyPath()
    {
        return propertyPath;
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
        final DataValidationError that = (DataValidationError) o;
        return propertyPath.equals( that.propertyPath );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), propertyPath );
    }
}
