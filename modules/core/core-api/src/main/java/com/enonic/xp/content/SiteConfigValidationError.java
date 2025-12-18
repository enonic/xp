package com.enonic.xp.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyPath;

public final class SiteConfigValidationError
    extends DataValidationError
{
    private final ApplicationKey applicationKey;

    SiteConfigValidationError( final PropertyPath propertyPath, final ValidationErrorCode errorCode, final String message,
                               final String i18n, final ApplicationKey applicationKey, final List<Object> args )
    {
        super( propertyPath, errorCode, message, i18n, args );
        this.applicationKey = Objects.requireNonNull( applicationKey );
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
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
        final SiteConfigValidationError that = (SiteConfigValidationError) o;
        return applicationKey.equals( that.applicationKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), applicationKey );
    }
}
